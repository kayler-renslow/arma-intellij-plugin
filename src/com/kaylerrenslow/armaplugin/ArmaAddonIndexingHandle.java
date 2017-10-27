package com.kaylerrenslow.armaplugin;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @see ArmaAddonsIndexingCallback
 * @see ArmaAddonsManager#loadAddons(ArmaAddonsProjectConfig)
 * @since 10/26/2017
 */
public interface ArmaAddonIndexingHandle {
	/**
	 * Invoke this method to cancel the indexing for the addon
	 */
	void cancel();

	/**
	 * @return the total indexing progress for the addon (ranged 0.0 - 1.0)
	 */
	double getCurrentWorkProgress();

	/**
	 * @return the progress for current indexing work the addon (ranged 0.0 - 1.0)
	 */
	double getTotalWorkProgress();

	/**
	 * @return the addon/mod's name (the @ prefixed name to be specific)
	 */
	@NotNull
	String getAddonName();
}
