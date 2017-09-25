package com.kaylerrenslow.armaplugin;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Used to represent a addonscfg.xml file.
 *
 * @author Kayler
 * @since 09/23/2017
 */
public interface ArmaAddonsProjectConfig {
	/**
	 * Returns a list of Addon directory names (i.e. "@MyAddon", "@My - Addon").
	 * The names are as they appear in one of {@link #getAddonsRoots()}.
	 * <p>
	 * This list specifies what addons to ignore.
	 *
	 * @return list of addon names to blacklist
	 */
	@NotNull
	List<String> getBlacklistedAddons();

	/**
	 * Returns a list of Addon directory names (i.e. "@MyAddon", "@My - Addon").
	 * The names are as they appear in one of {@link #getAddonsRoots()}.
	 * <p>
	 * This list specifies what addons to accept.
	 * If the list is empty, then all addons, except ones present in {@link #getBlacklistedAddons()},
	 * should be accepted.
	 *
	 * @return list of addon names to blacklist
	 */
	@NotNull
	List<String> getWhitelistedAddons();

	/**
	 * Returns a directory for where to store extracted content from addons for the IntelliJ plugin to use.
	 *
	 * @return a directory
	 */
	@NotNull
	String getAddonsReferenceDirectory();

	/**
	 * Returns a list of file directories that specify where Addons reside.
	 * This determines where {@link #getWhitelistedAddons()} and {@link #getBlacklistedAddons()} exist.
	 *
	 * @return list of directories
	 */
	@NotNull
	List<String> getAddonsRoots();
}
