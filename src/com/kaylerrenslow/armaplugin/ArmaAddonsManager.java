package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderParser;
import com.kaylerrenslow.armaDialogCreator.util.XmlUtil;
import com.kaylerrenslow.armaplugin.lang.ArmaPluginUserData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
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
		return addonsReadOnly;
	}

	//todo: document this method
	public void loadAddons(@NotNull ArmaAddonsProjectConfig config) {
		List<ArmaAddon> armaAddons;
		try {
			armaAddons = doLoadAddons(config);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		this.addons.clear();
		this.addons.addAll(armaAddons);
	}

	@NotNull
	private List<ArmaAddon> doLoadAddons(@NotNull ArmaAddonsProjectConfig config) throws Exception {
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
						return false;
					}

					return !useWhitelist || config.getWhitelistedAddons().contains(name);
				});
				if (files == null) {
					continue;
				}
				for (File addonDir : files) {
					addonHelpers.add(new ArmaAddonHelper(addonDir));
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
		}

		for (ArmaAddonHelper helper : addonHelpers) {
			doWorkForAddonHelper(helper, refDir, armaTools, tempDir);
		}

		deleteDirectory(tempDir);

		List<ArmaAddon> addons = new ArrayList<>(addonHelpers.size());
		for (ArmaAddonHelper helper : addonHelpers) {
			addons.add(new ArmaAddonImpl(helper));
		}

		return addons;
	}

	private void doWorkForAddonHelper(@NotNull ArmaAddonHelper helper,
									  @NotNull File refDir, @NotNull File armaTools, @NotNull File tempDir) throws Exception {
		final List<File> extractDirs = Collections.synchronizedList(new ArrayList<>());
		final List<File> debinarizedConfigs = Collections.synchronizedList(new ArrayList<>());
		final List<File> sqfFiles = Collections.synchronizedList(new ArrayList<>());

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
					return;
				}
			}
			File[] pboFiles = addonsDir.listFiles((dir, name) -> name.endsWith(".pbo"));
			if (pboFiles == null) {
				return;
			}

			//make it so each partition of the files won't place more work than the other for threads
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

			Function<List<File>, Void> extractPbos = pboFilesToExtract -> {
				for (File pboFile : pboFilesToExtract) {
					File extractDir = new File(
							tempDir.getAbsolutePath() + "/" + helper.getAddonDirName() +
									"/" +
									pboFile.getName().substring(0, pboFile.getName().length() - ".pbo".length())
					);
					boolean success = false;
					try {
						success = ArmaTools.extractPBO(
								armaTools,
								pboFile,
								extractDir, 10 * 60 * 1000 /*10 minutes before suspend*/
						);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (!success) {
						System.err.println("ArmaAddonsManager.java: Couldn't extract pbo " + pboFile);
						continue;
					}
					extractDirs.add(extractDir);
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
				} else if (visit.getName().endsWith(".sqf")) {
					sqfFiles.add(visit);
				}
			}

			//make it so each partition of the files won't place more work than the other for threads
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

			Function<List<File>, Void> convertConfigBinFiles = configBinFilesToConvert -> {
				for (File configBinFile : configBinFilesToConvert) {
					String newPath = configBinFile.getParentFile().getAbsolutePath() + "/config.cpp";
					File debinarizedFile = new File(newPath);
					boolean success = false;
					try {
						success = ArmaTools.convertBinConfigToText(
								armaTools,
								configBinFile,
								debinarizedFile,
								10 * 1000 /*10 seconds*/
						);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (!success) {
						System.err.println("ArmaAddonsManager.java: Couldn't convert binarized config " + configBinFile);
						continue;
					}
					debinarizedConfigs.add(debinarizedFile);
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

		}

		{//parse the configs
			for (File configFile : debinarizedConfigs) {
				HeaderFile headerFile = HeaderParser.parse(configFile, configFile.getParentFile());
				helper.getParsedConfigs().add(headerFile);
			}
		}

		{//copy the files out of temp directory that we want to keep
			//create folder in reference directory
			File destDir = new File(refDir.getAbsolutePath() + "/" + helper.getAddonDirName());
			boolean mkdirs = destDir.mkdirs();
			if (!mkdirs) {
				System.err.println("ArmaAddonsManager.java: Couldn't create reference directory for " + helper.getAddonDirName());
				return;
			}

			//copy over sqf files into destDir and replicate folder structure in destDir
			LinkedList<File> toVisit = new LinkedList<>();
			LinkedList<File> traverseCopy = new LinkedList<>();
			toVisit.addAll(extractDirs);
			traverseCopy.add(destDir);
			while (!toVisit.isEmpty()) {
				File visit = toVisit.pop();
				File folderCopy = traverseCopy.pop();

				if (visit.isDirectory() && !extractDirs.contains(visit)) {
					File newFolder = new File(folderCopy.getAbsolutePath() + "/" + visit.getName());
					boolean mkdirs1 = newFolder.mkdirs();
					if (!mkdirs1) {
						System.err.println("ArmaAddonsManager.java: Couldn't create directories for " + newFolder);
						continue;
					}
				}

				File[] children = visit.listFiles();
				if (children != null) {
					for (File child : children) {
						if (sqfFiles.contains(child)) {
							try {
								Files.copy(
										child.toPath(),
										new File(destDir.getAbsolutePath() + "/" + child.getName()).toPath(),
										StandardCopyOption.REPLACE_EXISTING
								);
							} catch (IOException e) {
								e.printStackTrace();
								continue;
							}
						} else if (child.isDirectory()) {
							toVisit.push(child);
						}
					}
				}
			}
		}

		//delete extract directories to free up disk space for next addon extraction
		for (File extractDir : extractDirs) {
			deleteDirectory(extractDir);
		}
	}

	private static ArmaAddonsManager instance;

	@NotNull
	public static ArmaAddonsManager getAddonsManagerInstance() {
		if (instance == null) {
			instance = new ArmaAddonsManager();
		}
		return instance;
	}

	private static void deleteDirectory(@NotNull File directory) {
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
		directory.delete();
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
	public static ArmaAddonsProjectConfig parseAddonsConfig(@NotNull VirtualFile configFile, @NotNull Project project) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			return null;
		}
		Document doc;
		try {
			doc = builder.parse(new File(configFile.getPath()));
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

	}

	private static class ArmaAddonHelper {
		@NotNull
		private final File addonDirectory;
		private final List<HeaderFile> parsedConfigs = Collections.synchronizedList(new ArrayList<>());

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
	}

	private static class ArmaToolsWorkerThread extends Thread {
		public ArmaToolsWorkerThread(@NotNull Runnable target, int workerId) {
			super(target);
			setName("ArmaAddonsManager.ArmaToolsWorkerThread " + workerId);
		}
	}
}
