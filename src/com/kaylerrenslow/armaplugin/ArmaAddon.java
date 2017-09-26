package com.kaylerrenslow.armaplugin;

import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaDialogCreator.util.ReadOnlyList;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Kayler
 * @since 09/25/2017
 */
public interface ArmaAddon {

	/**
	 * @return a list of {@link HeaderFile} instances that each represent a parsed config.cpp
	 */
	@NotNull
	ReadOnlyList<HeaderFile> getConfigFiles();

	/**
	 * @return the folder with the "@" symbol prefixed
	 */
	@NotNull
	File getAddonDirectory();

	/**
	 * @return the name of the addon with the "@" symbol prefixed
	 */
	@NotNull
	String getAddonDirectoryName();

}
