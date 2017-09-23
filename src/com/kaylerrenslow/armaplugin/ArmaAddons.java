package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * @author Kayler
 * @since 09/22/2017
 */
public class ArmaAddons {
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


	@Nullable
	public static ArmaAddonsModuleConfig parsePluginAddonsConfig(@NotNull VirtualFile configFile) {
		return null;//todo
	}

	public interface ArmaAddonsModuleConfig {
		@NotNull
		List<File> getBlacklistedFiles();

		@NotNull
		List<File> getWhitelistedFiles();

		@NotNull
		File getAddonsExtractDirectory();
	}

	/*
	* <?xml blah blah>
	* <addons-cfg>
	*     <addons-root>D:\DATA\Steam\steamapps\common\Arma 3</addons-root>
	*     <addons-root>$PROJECT_DIR$/addons</addons-root> <!-- There can be multiple addons roots-->
	*         
	*     <extract-dir>D:\DATA\Steam\steamapps\common\Arma 3\armaplugin</extract-dir>
	*     <blacklist>
	*         <addon>@Exile</addon> <!-- This refers to the directory name in addons-root-->
	*     </blacklist>
	*     <whitelist>
	*         <addon>@OPTRE</addon> <!-- This refers to the directory name in addons-root-->
	*     </whitelist>
	* </addons-cfg>
	* */
}
