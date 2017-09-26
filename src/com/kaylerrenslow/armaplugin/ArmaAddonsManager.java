package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaDialogCreator.util.ReadOnlyList;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 09/22/2017
 */
public class ArmaAddonsManager {
	// We want to be able to control what gets kept after the extraction (models, sqf files, etc).
	// However, the config files should always be extracted. Reason is the user may not care about the implementation of the scripts.
	// We definitely don't need to keep the models or sounds or any other non-script related resource.

	// CAREFUL: when we delete files that are extracted and don't need, we need to make sure we aren't deleting files that were originally in the folder.
	// A user may accidentally select the wrong folder or misunderstand instructions and we don't want them to pay for it!
	// Idea: create a folder inside the extract folder in which we can extract everything. The folder we create shouldn't exist!
	// Once the extract is done, we move out everything we want to keep and then delete what remains, including the temp extract folder.
	// The temp extract folder is also where we would do binarized config conversion


	// We want a central location that lists where the mods are stored. We also want a central location of where the extracted contents go.
	// It would be ideal to have the option for many different extract locations, but also be able to detect when extraction isn't necessary.

	// We will want to make sure that we collect ALL config.cpp files. Some mods can have multiple. Thus, we will want a way
	// to programmatically merge the HeaderFiles or at least index them as if they were 1 file.

	// For the file that lists all the mods, we should allow blacklisting, whitelisting, and option to use everything in a directory.

	// Question: Where would we store the file that lists all the dependency mods?

	// For when we are extracting PBO files, we need a dialog to show progress on extraction and the like.

	private ArmaAddonsManager() {
	}

	private final List<ArmaAddon> addons = new ArrayList<>();
	private final ReadOnlyList<ArmaAddon> addonsReadOnly = new ReadOnlyList<>(addons);

	@NotNull
	public ReadOnlyList<ArmaAddon> getAddons() {
		return addonsReadOnly;
	}

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

		{ //find and extract all PBO's from each addon

			// Create a temp folder to extract the pbo in.
			// Make sure it doesn't exist to ensure we aren't overwriting/deleting existing data
			File tempDir;
			{
				String tempDirName = "_armaPluginTemp";
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

			{//extract pbo's into temp directory
				for (ArmaAddonHelper helper : addonHelpers) {
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
							continue;
						}
					}
					File[] pboFiles = addonsDir.listFiles((dir, name) -> name.endsWith(".pbo"));
					if (pboFiles == null) {
						continue;
					}
					//todo make this multithreaded
					for (File pboFile : pboFiles) {
						File extractDir = new File(
								tempDir.getAbsolutePath() + "/" + helper.getAddonDirName()
						);
						boolean success = ArmaTools.extractPBO(
								armaTools,
								pboFile,
								extractDir, 10 * 60 * 1000 /*10 minutes before suspend*/
						);
						if (!success) {
							System.err.println("Couldn't extract pbo " + pboFile);
							continue;
						}
						helper.setExtractDir(extractDir);
					}
				}
			}
			final String BINARIZED_CONFIG_NAME = "config.bin";
			{//de-binarize the configs
				for (ArmaAddonHelper helper : addonHelpers) {
					List<File> configBinFiles = new ArrayList<>();
					LinkedList<File> toVisit = new LinkedList<>();
					//locate all config.bin files
					if (helper.getExtractDir() != null) {
						toVisit.add(helper.getExtractDir());
					}
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

					//convert all config.bin files
					//todo make multithreaded
					for (File configBinFile : configBinFiles) {
						String newPath = configBinFile.getAbsolutePath();
						newPath = newPath.substring(0, newPath.length() - configBinFile.getName().length()) + "config.cpp";
						File debinarizedFile = new File(newPath);
						boolean success = ArmaTools.convertBinConfigToText(
								armaTools,
								configBinFile,
								debinarizedFile,
								10 * 1000 /*10 seconds*/
						);
						if (!success) {
							System.err.println("Couldn't convert binarized config " + configBinFile);
							continue;
						}
						helper.getDebinarizedConfigs().add(debinarizedFile);
					}
				}
			}


			//move the files out of temp directory that we want to keep
			//todo

			//delete temp directory (will also remove unnecessary files)
			//todo

			//parse the configs
			//todo
		}

		List<ArmaAddon> addons = new ArrayList<>(addonHelpers.size());
		for (ArmaAddonHelper helper : addonHelpers) {
			//todo fill addons list
		}

		return addons;
	}


	private static ArmaAddonsManager instance;

	@NotNull
	public static ArmaAddonsManager getAddonsManagerInstance() {
		if (instance == null) {
			instance = new ArmaAddonsManager();
		}
		return instance;
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


	private static class ArmaAddonHelper {
		@NotNull
		private final File addonDirectory;
		private final List<HeaderFile> parsedConfigs = new ArrayList<>();
		private final List<File> debinarizedConfigs = new ArrayList<>();
		private File extractDir;

		public ArmaAddonHelper(@NotNull File addonDirectory) {
			this.addonDirectory = addonDirectory;
		}

		@NotNull
		public List<HeaderFile> getParsedConfigs() {
			return parsedConfigs;
		}

		@Nullable
		public File getExtractDir() {
			return extractDir;
		}

		public void setExtractDir(@Nullable File extractDir) {
			this.extractDir = extractDir;
		}

		@NotNull
		public File getAddonDirectory() {
			return addonDirectory;
		}

		@NotNull
		public String getAddonDirName() {
			return addonDirectory.getName();
		}

		@NotNull
		public List<File> getDebinarizedConfigs() {
			return debinarizedConfigs;
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
}
