package com.kaylerrenslow.armaplugin;

/**
 * @author Kayler
 * @since 09/22/2017
 */
public class ArmaAddons {
	// We want to be able to control what gets kept after the extraction (models, sqf files, etc).
	// However, the config files should always be extracted. Reason is the user may not care about the implementation of the scripts.
	// We definitely don't need to keep the models or sounds or any other non-script related resource.

	// We want a central location that lists where the mods are stored. We also want a central location of where the extracted contents go.
	// It would be ideal to have the option for many different extract locations, but also be able to detect when extraction isn't necessary.

	// We will want to make sure that we collect ALL config.cpp files. Some mods can have multiple. Thus, we will want a way
	// to programmatically merge the HeaderFiles or at least index them as if they were 1 file.

	// For the file that lists all the mods, we should allow blacklisting, whitelisting, and option to use everything in a directory.

	// Question: Where would we store the file that lists all the dependency mods?

	// For when we are extracting PBO files, we need a dialog to show progress on extraction and the like.
}
