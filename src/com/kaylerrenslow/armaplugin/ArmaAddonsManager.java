package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.kaylerrenslow.armaDialogCreator.util.ReadOnlyList;
import com.kaylerrenslow.armaDialogCreator.util.XmlUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.ArrayList;
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
