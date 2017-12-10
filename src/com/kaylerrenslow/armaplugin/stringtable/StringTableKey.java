package com.kaylerrenslow.armaplugin.stringtable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A wrapper for {@link Key} that contains additional information
 *
 * @author Kayler
 * @since 12/10/2017
 */
public class StringTableKey {
	@NotNull
	private final Key key;
	@NotNull
	private final String containerPath;

	public StringTableKey(@NotNull Key key, @Nullable String containerPath) {
		this.key = key;
		this.containerPath = containerPath == null ? "" : containerPath;
	}

	/**
	 * @return the XML element that this class wraps
	 */
	@NotNull
	public Key getKey() {
		return key;
	}

	/**
	 * @return a user-friendly display path that shows the package and container to get to the key
	 */
	@NotNull
	public String getContainerPath() {
		return containerPath;
	}

	/**
	 * @return the key's id attribute
	 */
	@NotNull
	public String getID() {
		return key.getID().getRawText() == null ? "" : key.getID().getRawText();
	}
}
