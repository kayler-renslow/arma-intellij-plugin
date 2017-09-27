package com.kaylerrenslow.armaplugin;

import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

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
	 * @return the folder with the "@" symbol prefixed
	 */
	@NotNull
	File getAddonDirectory();

	/**
	 * @return the name of the addon with the "@" symbol prefixed
	 */
	@NotNull
	default String getAddonDirectoryName() {
		return getAddonDirectory().getName();
	}

}
