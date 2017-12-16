package com.kaylerrenslow.armaplugin;

import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author Kayler
 * @since 09/25/2017
 */
public interface ArmaAddon {

	/**
	 * @return a read-only list of {@link HeaderFile} instances that each represent a parsed config.cpp
	 */
	@NotNull
	List<HeaderFile> getConfigFiles();

	/**
	 * Get the the folder with the "@" symbol prefixed. This is not relative to the reference directory, rather,
	 * it is relative to where the addon is installed on the user's computer.
	 *
	 * @return the addon folder
	 * @see #getAddonDirectoryInReferenceDirectory()
	 */
	@NotNull
	File getAddonDirectory();

	/**
	 * Get the the folder with the "@" symbol prefixed. Unlike {@link #getAddonDirectory()}, this file path is
	 * relative to the reference directory.
	 *
	 * @return the addon folder
	 * @see #getAddonDirectory()
	 */
	@NotNull
	File getAddonDirectoryInReferenceDirectory();

	/**
	 * @return the name of the addon with the "@" symbol prefixed
	 */
	@NotNull
	default String getAddonDirectoryName() {
		return getAddonDirectory().getName();
	}

	/**
	 * Get a Map that contains all #define macros detected by the preprocessor. The key of the map is the name of the #define
	 * and the value is the result template text.
	 * <p>
	 * Example 1: #define VARIABLE myTemplateText(VARIABLE) //"VARIABLE" is key, "myTemplateText(VARIABLE)" is template text
	 * <p>
	 * Example 2: #define PARAM(A,B) A+B //"PARAM(A,B)" is key, "A+B" is template text
	 *
	 * @return a map
	 */
	@NotNull
	Map<String, String> getDefineMacros();

}
