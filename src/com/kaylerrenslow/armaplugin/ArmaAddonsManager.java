package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.project.Project;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderParseException;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderParser;
import com.kaylerrenslow.armaDialogCreator.util.XmlUtil;
import com.kaylerrenslow.armaplugin.ArmaAddonsIndexingCallback.Step;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 09/22/2017
 */
public class ArmaAddonsManager {
	private ArmaAddonsManager() {
	}

	private final List<ArmaAddon> addons = new ArrayList<>();
	private final List<ArmaAddon> addonsReadOnly = Collections.unmodifiableList(addons);

	/**
	 * @return a read-only list containing all addons
	 */
	@NotNull
	public List<ArmaAddon> getAddons() {
		synchronized (instance) {
			return addonsReadOnly;
		}
	}

	/**
	 * Will load addons into {@link #getAddons()}.
	 *
	 * @param config   the config to use
	 * @param callback
	 */
	//todo: document this method
	//todo: we should check to see if we need to re-extract each addon. If we don't need to extract it, load it from reference directory
	public void loadAddonsAsync(@NotNull ArmaAddonsProjectConfig config, @Nullable File logFile, @NotNull ArmaAddonsIndexingCallback callback) {
		Thread t = new Thread(() -> {
			List<ArmaAddon> armaAddons;
			ArmaToolsCallbackForwardingThread forwardingThread = new ArmaToolsCallbackForwardingThread(callback, logFile);
			forwardingThread.start();
			try {
				armaAddons = doLoadAddons(config, logFile, callback, forwardingThread);
			} catch (Exception e) {
				e.printStackTrace();
				forwardingThread.logError("Couldn't complete indexing addons", e);
				return;
			} finally {
				forwardingThread.log("[EXIT]\n\n");
				forwardingThread.closeThread();
			}
			synchronized (instance) {
				this.addons.clear();
				this.addons.addAll(armaAddons);
			}
		}, "ArmaAddonsManager - Load Addons");
		t.start();
	}

	@NotNull
	private List<ArmaAddon> doLoadAddons(@NotNull ArmaAddonsProjectConfig config,
										 @Nullable File logFile, @NotNull ArmaAddonsIndexingCallback callback,
										 @NotNull ArmaToolsCallbackForwardingThread forwardingThread) throws Exception {
		ResourceBundle bundle = getBundle();

		File refDir = new File(config.getAddonsReferenceDirectory());
		if (refDir.exists() && !refDir.isDirectory()) {
			throw new IllegalArgumentException("reference directory isn't a directory");
		}
		if (!refDir.exists()) {
			boolean mkdirs = refDir.mkdirs();
			if (!mkdirs) {
				throw new IllegalStateException("couldn't make directories for the reference directory");
			}
		}
		File armaTools = ArmaPluginUserData.getInstance().getArmaToolsDirectory();
		if (armaTools == null) {
			throw new IllegalStateException("arma tools directory isn't set");
		}

		List<ArmaAddonHelper> addonHelpers = new ArrayList<>();

		{//Collect all @ prefixed folders that aren't blacklisted and is whitelisted (ignore whitelist when it's empty).
			List<File> addonRoots = new ArrayList<>(config.getAddonsRoots().size());
			for (String addonRootPath : config.getAddonsRoots()) {
				File f = new File(addonRootPath);
				if (!f.exists()) {
					continue;
				}
				if (!f.isDirectory()) {
					continue;
				}
				forwardingThread.log("Found addon root " + f.getAbsolutePath());
				addonRoots.add(f);
			}

			boolean useWhitelist = !config.getWhitelistedAddons().isEmpty();

			for (File addonRoot : addonRoots) {
				File[] files = addonRoot.listFiles((dir, name) -> {
					if (name.length() == 0) {
						return false;
					}
					if (name.charAt(0) != '@') {
						return false;
					}
					if (config.getBlacklistedAddons().contains(name)) {
						forwardingThread.log("Addon excluded (blacklisted):" + name);
						return false;
					}

					return !useWhitelist || config.getWhitelistedAddons().contains(name);
				});
				if (files == null) {
					continue;
				}
				for (File addonDir : files) {
					addonHelpers.add(new ArmaAddonHelper(addonDir));
					forwardingThread.log("Addon directory marked for indexing: " + addonDir.getAbsolutePath());
				}
			}
		}

		File tempDir;
		{// Create a temp folder to extract the pbo in.
			String tempDirName = "_armaPluginTemp";

			// Make sure directory doesn't exist to ensure we aren't overwriting/deleting existing data
			while (true) {
				boolean matched = false;
				File[] files = refDir.listFiles();
				if (files == null) {
					throw new IllegalStateException("files is null despite refDir being a directory");
				}
				for (File f : files) {
					if (f.getName().equals(tempDirName)) {
						tempDirName = tempDirName + "_";
						matched = true;
						break;
					}
				}
				if (!matched) {
					break;
				}
			}
			tempDir = new File(refDir.getAbsolutePath() + "/" + tempDirName);
			boolean mkdirs = tempDir.mkdirs();
			if (!mkdirs) {
				throw new IllegalStateException("couldn't make the temp directory for extracting");
			}
			forwardingThread.log("Temp directory for addons extraction:" + tempDir.getAbsolutePath());
		}

		for (ArmaAddonHelper helper : addonHelpers) {
			if (helper.isCancelled()) {
				continue;
			}

			//reason for passing extractDirs instead of placing it in doWorkForAddonHelper
			//is because the addon could be cancelled half way through pbo extraction and we want to make sure
			//the data is cleaned up
			final List<File> extractDirs = Collections.synchronizedList(new ArrayList<>());
			forwardingThread.workBegin(helper);
			doWorkForAddonHelper(helper, refDir, armaTools, tempDir, forwardingThread, extractDirs);

			//delete extract directories to free up disk space for next addon extraction
			forwardingThread.stepStart(helper, Step.Cleanup);
			for (File extractDir : extractDirs) {
				boolean success = deleteDirectory(extractDir);
				if (success) {
					forwardingThread.log(
							String.format(bundle.getString("deleted-temp-directory-f"), extractDir)
					);
				} else {
					forwardingThread.warningMessage(
							helper,
							String.format(bundle.getString("failed-to-delete-temp-directory-f"), extractDir),
							null
					);
				}
			}
			forwardingThread.stepFinish(helper, Step.Cleanup);
			forwardingThread.finishedAddonIndex(helper);
		}

		boolean success = deleteDirectory(tempDir);
		if (success) {
			forwardingThread.log(
					String.format(bundle.getString("deleted-temp-directory-f"), tempDir)
			);
		} else {
			forwardingThread.logWarning(
					String.format(bundle.getString("failed-to-delete-temp-directory-f"), tempDir),
					null
			);
		}

		List<ArmaAddon> addons = new ArrayList<>(addonHelpers.size());
		for (ArmaAddonHelper helper : addonHelpers) {
			if (helper.isCancelled()) {
				forwardingThread.log("Addon cancelled: " + helper.getAddonDirName());
				continue;
			}
			forwardingThread.log("Addon finished: " + helper.getAddonDirName());
			addons.add(new ArmaAddonImpl(helper));
		}

		return addons;
	}

	private void doWorkForAddonHelper(@NotNull ArmaAddonHelper helper,
									  @NotNull File refDir, @NotNull File armaTools, @NotNull File tempDir,
									  @NotNull ArmaToolsCallbackForwardingThread forwardingThread,
									  @NotNull List<File> extractDirs) throws Exception {
		ResourceBundle bundle = getBundle();

		final List<File> debinarizedConfigs = Collections.synchronizedList(new ArrayList<>());

		{//extract pbo's into temp directory

			File addonsDir = null;
			{ //locate the "addons" folder, which contains all the pbo files
				File[] addonDirFiles = helper.getAddonDirectory().listFiles();
				if (addonDirFiles == null) {
					throw new IllegalStateException("addon directory isn't a directory: " + helper.getAddonDirectory());
				}
				for (File addonDirFile : addonDirFiles) {
					if (!addonDirFile.getName().equalsIgnoreCase("addons")) {
						continue;
					}
					addonsDir = addonDirFile;
					break;
				}
				if (addonsDir == null) {
					forwardingThread.warningMessage(helper,
							String.format(
									bundle.getString("couldnt-find-addons-dir-f"),
									helper.getAddonDirectory().getAbsolutePath()
							), null
					);
					return;
				}
			}
			File[] pboFiles = addonsDir.listFiles((dir, name) -> name.endsWith(".pbo"));
			if (pboFiles == null) {
				forwardingThread.warningMessage(helper,
						String.format(
								bundle.getString("no-pbos-were-in-addons-dir-f"),
								helper.getAddonDirectory().getAbsolutePath()
						), null
				);
				return;
			}

			//make it so each thread doesn't do more work than the other. Also, partition the data.
			Arrays.sort(pboFiles, Comparator.comparingLong(File::length));
			List<File> left = new ArrayList<>(pboFiles.length);
			List<File> right = new ArrayList<>(pboFiles.length);
			int leftCapacity = 0;
			int rightCapacity = 0;
			for (int i = pboFiles.length - 1; i >= 0; i--) {//iterate backwards since array is sorted A-Z
				File file = pboFiles[i];
				if (leftCapacity < rightCapacity) {
					left.add(file);
					leftCapacity += file.length();
				} else {
					right.add(file);
					rightCapacity += file.length();
				}
			}


			forwardingThread.stepStart(helper, Step.ExtractPBOs);

			Function<List<File>, Void> extractPbos = pboFilesToExtract -> {
				StringBuilder sb = new StringBuilder();
				sb.append("Extracting PBO's on thread ").append(Thread.currentThread().getName()).append(": [");
				int i = 0;
				for (File pboFile : pboFilesToExtract) {
					sb.append(pboFile.getName());
					i++;
					if (i < pboFilesToExtract.size()) {
						sb.append(", ");
					}
				}
				sb.append(']');
				forwardingThread.log(sb.toString());

				for (File pboFile : pboFilesToExtract) {
					if (helper.isCancelled()) {
						return null;
					}
					File extractDir = new File(
							tempDir.getAbsolutePath() + "/" + helper.getAddonDirName() +
									"/" +
									pboFile.getName().substring(0, pboFile.getName().length() - ".pbo".length())
					);
					boolean mkdirs = extractDir.mkdirs();
					if (!mkdirs) {
						forwardingThread.errorMessage(
								helper, String.format(
										bundle.getString("failed-to-create-temp-directory-f"),
										extractDir.getAbsolutePath(),
										helper.getAddonDirName()
								), null
						);
						return null;
					}

					extractDirs.add(extractDir);
					forwardingThread.log("Created extract directory: " + extractDir.getAbsolutePath());
					boolean success = false;
					Exception e = null;
					try {
						success = ArmaTools.extractPBO(
								armaTools,
								pboFile,
								extractDir, 10 * 60 * 1000 /*10 minutes before suspend*/
						);
						forwardingThread.message(helper,
								String.format(
										bundle.getString("extracted-pbo-f"),
										pboFile.getName(),
										helper.getAddonDirName()
								)
						);
					} catch (IOException e1) {
						e = e1;
					}
					if (!success) {
						forwardingThread.errorMessage(helper,
								String.format(
										bundle.getString("couldnt-extract-pbo-f"),
										pboFile.getName(), helper.getAddonDirName()
								), e
						);
					}
				}
				return null;
			};
			ArmaToolsWorkerThread t1 = new ArmaToolsWorkerThread(() -> {
				extractPbos.apply(left);
			}, 1
			);
			ArmaToolsWorkerThread t2 = new ArmaToolsWorkerThread(() -> {
				extractPbos.apply(right);
			}, 2
			);
			t1.start();
			t2.start();
			t1.join();
			t2.join();
			forwardingThread.message(helper,
					String.format(
							bundle.getString("extracted-all-pbo-f"),
							helper.getAddonDirName()
					)
			);
			forwardingThread.stepFinish(helper, Step.ExtractPBOs);
		}
		{//de-binarize the configs and locate all sqf files
			final String BINARIZED_CONFIG_NAME = "config.bin";
			List<File> configBinFiles = new ArrayList<>();
			LinkedList<File> toVisit = new LinkedList<>();
			//locate all config.bin files
			toVisit.addAll(extractDirs);
			while (!toVisit.isEmpty()) {
				File visit = toVisit.removeFirst();
				File[] children = visit.listFiles();
				if (children != null) {
					Collections.addAll(toVisit, children);
				}
				if (visit.getName().equalsIgnoreCase(BINARIZED_CONFIG_NAME)) {
					configBinFiles.add(visit);
				}
			}

			//make it so each thread doesn't do more work than the other. Also, partition the data.
			configBinFiles.sort(Comparator.comparingLong(File::length));
			List<File> left = new ArrayList<>(configBinFiles.size());
			List<File> right = new ArrayList<>(configBinFiles.size());
			int leftCapacity = 0;
			int rightCapacity = 0;
			for (int i = configBinFiles.size() - 1; i >= 0; i--) {//iterate backwards since array is sorted A-Z
				File file = configBinFiles.get(i);
				if (leftCapacity < rightCapacity) {
					left.add(file);
					leftCapacity += file.length();
				} else {
					right.add(file);
					rightCapacity += file.length();
				}
			}

			forwardingThread.stepStart(helper, Step.DeBinarizeConfigs);

			Function<List<File>, Void> convertConfigBinFiles = configBinFilesToConvert -> {
				StringBuilder sb = new StringBuilder();
				sb.append("DeBinarizing config.bin files on thread ").append(Thread.currentThread().getName()).append(": [\n");
				int i = 0;
				for (File configBinFile : configBinFiles) {
					sb.append('\t');
					sb.append(configBinFile.getAbsolutePath());
					sb.append('\n');
					i++;
					if (i < configBinFiles.size()) {
						sb.append(", ");
					}
				}
				sb.append('\n');
				sb.append(']');
				forwardingThread.log(sb.toString());

				for (File configBinFile : configBinFilesToConvert) {
					if (helper.isCancelled()) {
						return null;
					}
					String newPath = configBinFile.getParentFile().getAbsolutePath() + "/config.cpp";
					File debinarizedFile = new File(newPath);
					Exception e = null;
					boolean success = false;
					try {
						success = ArmaTools.convertBinConfigToText(
								armaTools,
								configBinFile,
								debinarizedFile,
								10 * 1000 /*10 seconds*/
						);
					} catch (IOException e1) {
						e = e1;
					}
					if (!success) {
						forwardingThread.errorMessage(helper,
								String.format(
										bundle.getString("couldnt-debinarize-config-f"),
										configBinFile.getAbsolutePath(), helper.getAddonDirName()
								), e
						);
						continue;
					}
					debinarizedConfigs.add(debinarizedFile);
					forwardingThread.message(helper,
							String.format(
									bundle.getString("debinarized-config-f"),
									helper.getAddonDirName(), configBinFile.getAbsolutePath()
							)
					);
				}
				return null;
			};
			ArmaToolsWorkerThread t1 = new ArmaToolsWorkerThread(() -> {
				convertConfigBinFiles.apply(left);
			}, 1
			);
			ArmaToolsWorkerThread t2 = new ArmaToolsWorkerThread(() -> {
				convertConfigBinFiles.apply(right);
			}, 2
			);
			t1.start();
			t2.start();
			t1.join();
			t2.join();
			forwardingThread.message(
					helper,
					String.format(
							bundle.getString("debinarized-all-config-f"),
							helper.getAddonDirName()
					)
			);
			forwardingThread.stepFinish(helper, Step.DeBinarizeConfigs);
		}

		{//parse the configs
			forwardingThread.stepStart(helper, Step.ParseConfigs);
			for (File configFile : debinarizedConfigs) {
				if (helper.isCancelled()) {
					return;
				}
				try {
					HeaderFile headerFile = HeaderParser.parse(configFile, configFile.getParentFile());
					helper.getParsedConfigs().add(headerFile);
					forwardingThread.message(helper,
							String.format(
									bundle.getString("parsed-config-f"),
									configFile.getAbsolutePath()
							)
					);
				} catch (HeaderParseException e) {
					forwardingThread.errorMessage(helper,
							String.format(
									bundle.getString("couldnt-parse-config-f"),
									configFile.getAbsolutePath()
							), e
					);
				}
			}
			forwardingThread.message(helper,
					String.format(
							bundle.getString("parsed-all-config-f"),
							helper.getAddonDirName()
					)
			);
			forwardingThread.stepFinish(helper, Step.ParseConfigs);
		}

		{//copy the files out of temp directory that we want to keep
			//create folder in reference directory
			File destDir = new File(refDir.getAbsolutePath() + "/" + helper.getAddonDirName());
			boolean mkdirs = destDir.mkdirs();
			if (!mkdirs) {
				forwardingThread.errorMessage(
						helper,
						String.format(
								bundle.getString("failed-to-create-reference-directory-f"),
								destDir.getAbsolutePath(),
								helper.getAddonDirName()
						), null
				);
				return;
			}

			forwardingThread.stepStart(helper, Step.SaveReferences);

			//copy over sqf and header files into destDir and replicate folder structure in destDir
			LinkedList<File> toVisit = new LinkedList<>();
			LinkedList<File> traverseCopy = new LinkedList<>();
			for (File extractDir : extractDirs) {
				toVisit.add(extractDir);
				File folderCopy = new File(destDir.getAbsolutePath() + "/" + extractDir.getName());
				traverseCopy.add(folderCopy);
				boolean mkdirs1 = folderCopy.mkdirs();
				if (!mkdirs1) {
					forwardingThread.errorMessage(
							helper,
							String.format(
									bundle.getString("failed-to-create-reference-directory-f"),
									folderCopy.getAbsolutePath(),
									helper.getAddonDirName()
							), null
					);
				}
			}
			while (!toVisit.isEmpty()) {
				File visit = toVisit.pop();
				File folderCopy = traverseCopy.pop();

				File[] children = visit.listFiles();
				if (children != null) {
					for (File child : children) {
						if (child.isFile() && child.getName().endsWith(".sqf")
								|| child.getName().endsWith(".cpp")
								|| child.getName().endsWith(".h")
								|| child.getName().endsWith(".hh")
								|| child.getName().endsWith(".hpp")) {
							File target = new File(folderCopy.getAbsolutePath() + "/" + child.getName());
							try {
								Files.copy(
										child.toPath(),
										target.toPath(),
										StandardCopyOption.REPLACE_EXISTING
								);
								forwardingThread.message(
										helper,
										String.format(
												bundle.getString("copied-file-to-f"),
												child.getAbsolutePath(),
												target.getAbsolutePath()
										)
								);
							} catch (IOException e) {
								forwardingThread.errorMessage(
										helper,
										String.format(
												bundle.getString("couldnt-copy-file-to-f"),
												child.getAbsolutePath(),
												target.getAbsolutePath()
										), e
								);
								continue;
							}
						} else if (child.isDirectory()) {
							toVisit.push(child);

							File newFolder = new File(folderCopy.getAbsolutePath() + "/" + visit.getName());
							boolean mkdirs1 = newFolder.mkdirs();
							if (!mkdirs1) {
								forwardingThread.errorMessage(
										helper,
										String.format(
												bundle.getString("failed-to-create-directory-f"),
												newFolder.getAbsolutePath()
										), null
								);
								continue;
							}
							traverseCopy.push(newFolder);
						}
					}
				}
			}
			forwardingThread.stepFinish(helper, Step.SaveReferences);
		}
	}

	private ResourceBundle getBundle() {
		return ResourceBundle.getBundle("com.kaylerrenslow.armaplugin.ArmaAddonsManagerBundle");
	}

	private static final ArmaAddonsManager instance = new ArmaAddonsManager();

	@NotNull
	public static ArmaAddonsManager getAddonsManagerInstance() {
		return instance;
	}

	private static boolean deleteDirectory(@NotNull File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
		}
		return directory.delete();
	}


	/**
	 * Used to parse addonscfg.xml file. This method will also parse any macros present in the xml. For instance, the macro
	 * $PROJECT_DIR$ will resolve to the project's directory. If the project's directory can't be resolved,
	 * "." will be the result of the macro.
	 *
	 * @param configFile the .xml config file
	 * @param project    the project instance
	 * @return the config, or null if couldn't be created
	 */
	@Nullable
	public static ArmaAddonsProjectConfig parseAddonsConfig(@NotNull File configFile, @NotNull Project project) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			return null;
		}
		Document doc;
		try {
			doc = builder.parse(configFile);
		} catch (Exception e) {
			return null;
		}
		//https://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		/*
		* <?xml blah blah>
		* <addons-cfg>
		*     <roots>
		*         <addons-root>D:\DATA\Steam\steamapps\common\Arma 3</addons-root>
		*         <addons-root>$PROJECT_DIR$/addons</addons-root> <!-- There can be multiple addons roots-->
		*     </roots>
		*
		*     <reference-dir>D:\DATA\Steam\steamapps\common\Arma 3\armaplugin</reference-dir> <!-- This is the place where addon's extract pbo contents are stored-->
		*     <blacklist>
		*         <addon>@Exile</addon> <!-- This refers to the directory name in addons-root-->
		*     </blacklist>
		*     <whitelist> <!-- If whitelist has no addons, everything will be used, except for blacklist's addons-->
		*         <addon>@OPTRE</addon> <!-- This refers to the directory name in addons-root-->
		*     </whitelist>
		* </addons-cfg>
		*
		* MACROS:
		* $PROJECT_DIR$: IntelliJ project directory
		*/

		List<String> blacklistedAddons = new ArrayList<>();
		List<String> whitelistedAddons = new ArrayList<>();
		List<String> addonsRoots = new ArrayList<>();
		String addonsReferenceDirectory = null;

		{ //get addons roots
			List<Element> addonRootContainerElements = XmlUtil.getChildElementsWithTagName(doc.getDocumentElement(), "roots");
			for (Element addonRootContainerElement : addonRootContainerElements) {
				List<Element> addonsRootElements = XmlUtil.getChildElementsWithTagName(addonRootContainerElement, "addons-root");
				for (Element addonsRootElement : addonsRootElements) {
					addonsRoots.add(XmlUtil.getImmediateTextContent(addonsRootElement));
				}
			}
		}
		{ //get blacklisted files
			List<Element> blacklistContainerElements = XmlUtil.getChildElementsWithTagName(doc.getDocumentElement(), "blacklist");
			for (Element blacklistContainerElement : blacklistContainerElements) {
				List<Element> addonElements = XmlUtil.getChildElementsWithTagName(blacklistContainerElement, "addon");
				for (Element addonElement : addonElements) {
					blacklistedAddons.add(XmlUtil.getImmediateTextContent(addonElement));
				}
			}
		}
		{ //get whitelisted files
			List<Element> whitelistContainerElements = XmlUtil.getChildElementsWithTagName(doc.getDocumentElement(), "whitelist");
			for (Element whitelistContainerElement : whitelistContainerElements) {
				List<Element> addonElements = XmlUtil.getChildElementsWithTagName(whitelistContainerElement, "addon");
				for (Element addonElement : addonElements) {
					whitelistedAddons.add(XmlUtil.getImmediateTextContent(addonElement));
				}
			}
		}
		{ //get addons reference directory
			List<Element> referenceDirElements = XmlUtil.getChildElementsWithTagName(doc.getDocumentElement(), "reference-dir");
			if (referenceDirElements.size() > 0) {
				addonsReferenceDirectory = XmlUtil.getImmediateTextContent(referenceDirElements.get(0));
			}
			if (addonsReferenceDirectory == null) {
				return null;
			}
		}

		{ //convert macros into their values
			Function<String, String> evalMacros = s -> {
				String projectDir = project.getBasePath();
				if (projectDir == null) {
					projectDir = ".";
				}
				return s.replaceAll("\\$PROJECT_DIR\\$", projectDir);
			};

			List<String> addonsRootsTemp = new ArrayList<>();
			for (String addonRoot : addonsRoots) {
				addonsRootsTemp.add(evalMacros.apply(addonRoot));
			}
			addonsRoots.clear();
			addonsRoots.addAll(addonsRootsTemp);

			addonsReferenceDirectory = evalMacros.apply(addonsReferenceDirectory);
		}

		return new ArmaAddonsProjectConfigImpl(blacklistedAddons, whitelistedAddons, addonsRoots, addonsReferenceDirectory);
	}

	private static class ArmaAddonImpl implements ArmaAddon {

		private final List<HeaderFile> configFiles;
		private final File addonDirectory;

		public ArmaAddonImpl(@NotNull ArmaAddonHelper helper) {
			this.configFiles = Collections.unmodifiableList(helper.getParsedConfigs());
			this.addonDirectory = helper.getAddonDirectory();
		}

		@NotNull
		@Override
		public List<HeaderFile> getConfigFiles() {
			return configFiles;
		}

		@NotNull
		@Override
		public File getAddonDirectory() {
			return addonDirectory;
		}

		@Override
		public String toString() {
			return "ArmaAddonImpl{" +
					"configFiles=" + configFiles +
					", addonDirectory=" + addonDirectory +
					'}';
		}
	}

	private static class ArmaAddonHelper implements ArmaAddonIndexingHandle {
		private final File addonDirectory;
		private final List<HeaderFile> parsedConfigs = new ArrayList<>();
		private volatile double currentWorkProgress = 0;
		private volatile double totalWorkProgress = 0;
		private volatile boolean cancelled = false;

		public ArmaAddonHelper(@NotNull File addonDirectory) {
			this.addonDirectory = addonDirectory;
		}

		@NotNull
		public List<HeaderFile> getParsedConfigs() {
			return parsedConfigs;
		}

		@NotNull
		public File getAddonDirectory() {
			return addonDirectory;
		}

		@NotNull
		public String getAddonDirName() {
			return addonDirectory.getName();
		}

		@Override
		public void cancel() {
			cancelled = true;
		}

		@Override
		public boolean isCancelled() {
			return cancelled;
		}

		@Override
		public double getCurrentWorkProgress() {
			return currentWorkProgress;
		}

		@Override
		public double getTotalWorkProgress() {
			return totalWorkProgress;
		}

		public void setCurrentWorkProgress(double d) {
			currentWorkProgress = d;
		}

		public void setTotalWorkProgress(double d) {
			totalWorkProgress = d;
		}

		@NotNull
		@Override
		public String getAddonName() {
			return getAddonDirName();
		}
	}

	private static class ArmaAddonsProjectConfigImpl implements ArmaAddonsProjectConfig {

		@NotNull
		private final List<String> blacklistedAddons;
		@NotNull
		private final List<String> whitelistedAddons;
		@NotNull
		private final List<String> addonsRoots;
		@NotNull
		private final String addonsReferenceDirectory;

		public ArmaAddonsProjectConfigImpl(@NotNull List<String> blacklistedAddons,
										   @NotNull List<String> whitelistedAddons,
										   @NotNull List<String> addonsRoots,
										   @NotNull String addonsReferenceDirectory) {
			this.blacklistedAddons = blacklistedAddons;
			this.whitelistedAddons = whitelistedAddons;
			this.addonsRoots = addonsRoots;
			this.addonsReferenceDirectory = addonsReferenceDirectory;
		}

		@NotNull
		@Override
		public List<String> getBlacklistedAddons() {
			return blacklistedAddons;
		}

		@NotNull
		@Override
		public List<String> getWhitelistedAddons() {
			return whitelistedAddons;
		}

		@NotNull
		@Override
		public String getAddonsReferenceDirectory() {
			return addonsReferenceDirectory;
		}

		@NotNull
		@Override
		public List<String> getAddonsRoots() {
			return addonsRoots;
		}

		@Override
		public String toString() {
			return "ArmaAddonsProjectConfigImpl{" +
					"blacklist=" +
					blacklistedAddons +
					";whitelist=" +
					whitelistedAddons +
					";addonRoots=" +
					addonsRoots +
					";referenceDirectory=" +
					addonsReferenceDirectory +
					'}';
		}
	}

	private static class ArmaToolsWorkerThread extends Thread {
		public ArmaToolsWorkerThread(@NotNull Runnable target, int workerId) {
			super(target);
			setName("ArmaAddonsManager.ArmaToolsWorkerThread " + workerId);
		}
	}

	private static class ArmaToolsCallbackForwardingThread extends Thread implements ArmaAddonsIndexingCallback {
		private final LinkedBlockingQueue<Runnable> forwardingQ = new LinkedBlockingQueue<>();
		private final Runnable EXIT_THREAD = () -> {
		};
		@NotNull
		private final ArmaAddonsIndexingCallback callback;
		private PrintWriter logger;
		private int loggerBufferSize = 0;
		private final Object loggerLock = new Object();

		public ArmaToolsCallbackForwardingThread(@NotNull ArmaAddonsIndexingCallback callback, @Nullable File logFile) {
			this.callback = callback;
			setName("ArmaAddonsManager - Callback Thread");
			if (logFile != null) {
				try {
					logger = new PrintWriter(logFile);
				} catch (IOException e) {
					logger = null;
				}
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					Runnable take = forwardingQ.take();
					if (take == EXIT_THREAD) {
						return;
					}
				} catch (InterruptedException ignore) {
				}
			}
		}

		@Override
		public void workBegin(@NotNull ArmaAddonIndexingHandle handle) {
			forwardingQ.add(() -> {
				callback.workBegin(handle);
			});
		}

		@Override
		public void totalWorkProgressUpdate(@NotNull ArmaAddonIndexingHandle handle, double progress) {
			forwardingQ.add(() -> {
				callback.totalWorkProgressUpdate(handle, progress);
			});
		}

		@Override
		public void currentWorkProgressUpdate(@NotNull ArmaAddonIndexingHandle handle, double progress) {
			forwardingQ.add(() -> {
				callback.currentWorkProgressUpdate(handle, progress);
			});
		}

		@Override
		public void message(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message) {
			log(message);
			forwardingQ.add(() -> {
				callback.message(handle, message);
			});
		}

		@Override
		public void errorMessage(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message, @Nullable Exception e) {
			logError(message, e);
			forwardingQ.add(() -> {
				callback.errorMessage(handle, message, e);
			});
		}

		@Override
		public void warningMessage(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message, @Nullable Exception e) {
			logWarning(message, e);
			forwardingQ.add(() -> {
				callback.warningMessage(handle, message, e);
			});
		}

		@Override
		public void stepStart(@NotNull ArmaAddonIndexingHandle handle, @NotNull Step newStep) {
			forwardingQ.add(() -> {
				callback.stepStart(handle, newStep);
			});
		}

		@Override
		public void stepFinish(@NotNull ArmaAddonIndexingHandle handle, @NotNull Step stepFinished) {
			forwardingQ.add(() -> {
				callback.stepFinish(handle, stepFinished);
			});
		}

		@Override
		public void finishedAddonIndex(@NotNull ArmaAddonIndexingHandle handle) {
			forwardingQ.add(() -> {
				callback.finishedAddonIndex(handle);
			});
		}

		public void closeThread() {
			forwardingQ.add(EXIT_THREAD);
			if (logger != null) {
				synchronized (loggerLock) {
					try {
						logger.flush();
						logger.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void log(@NotNull String message) {
			if (logger == null) {
				//don't need to synchronize this check since logger is immutable after constructor
				return;
			}

			synchronized (loggerLock) {
				logger.append(message);
				logger.append("\n");
				loggerBufferSize += message.length() + 1;//+1 for \n
				if (loggerBufferSize >= 1000) {
					logger.flush();
					loggerBufferSize = 0;
				}
			}
		}

		public void logError(@NotNull String message, @Nullable Exception e) {
			synchronized (loggerLock) {
				log("[ERROR] " + message);
				if (e != null) {
					log(ArmaPluginUtil.getExceptionString(e));
				}
			}
		}

		public void logWarning(@NotNull String message, @Nullable Exception e) {
			synchronized (loggerLock) {
				log("[WARNING] " + message);
				if (e != null) {
					log(ArmaPluginUtil.getExceptionString(e));
				}
			}
		}
	}
}
