package com.kaylerrenslow.armaplugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Contains a set of methods to track the progress of Arma Addon/Mod indexing progress from
 * {@link ArmaAddonsManager#loadAddonsAsync(ArmaAddonsProjectConfig, File, ArmaAddonsIndexingCallback)}.
 * Each method is invoked for one particular addon, except for {@link ArmaAddonsIndexingCallback#finishedIndex() and {@link ArmaAddonsIndexingCallback#startedIndex(ArmaAddonsIndexingData) }}.
 * <p>
 * All methods will <b>not</b> run on the JavaFX Thread of Java Swing Thread.
 * Instead, the methods are invoked from a temporary thread.
 *
 * @author Kayler
 * @since 10/26/2017
 */
public interface ArmaAddonsIndexingCallback {
	enum Step {
		/**
		 * This step describes when the addon's individual PBO's are being extracted. This step will usually take the longest.
		 */
		ExtractPBOs,
		/**
		 * This step is for when the addon's config.bin files are being debinarized
		 */
		DeBinarizeConfigs,
		/**
		 * This step is for parsing debinarized config.bin files
		 */
		ParseConfigs,
		/**
		 * This step is for caching/saving SQF files and debinarized config files
		 */
		SaveReferences,
		/**
		 * This step is for deleting any unnecessary files created from the PBO extract.
		 * Files include model files (.p3d), and audio files (.ogg and .wss)
		 */
		Cleanup
	}

	/**
	 * This is invoked when a mod is beginning to get indexed. A {@link ArmaAddonIndexingHandle} is passed that contains
	 * methods to cancel the indexing of a specific addon.
	 *
	 * @param handle a handle for the addon
	 */
	void indexStartedForAddon(@NotNull ArmaAddonIndexingHandle handle);

	/**
	 * Invoked when the total overall work for an addon has been updated.
	 * The total work for the addon will reach 100% when all of its parts are complete.
	 * When the total work for the addon reaches 100%, {@link #indexFinishedForAddon(ArmaAddonIndexingHandle)} will be invoked.
	 *
	 * @param handle   the addon's handle
	 * @param progress new progress value for the addon (ranged 0.0-1.0)
	 * @see #currentWorkProgressUpdate(ArmaAddonIndexingHandle, double)
	 */
	void totalWorkProgressUpdate(@NotNull ArmaAddonIndexingHandle handle, double progress);

	/**
	 * Invoked when a partition of overall work for an addon has been updated.
	 * The total work for the addon will reach 100% when all of its parts are complete.
	 *
	 * @param handle   the addon's handle
	 * @param progress new progress value for the addon (ranged 0.0-1.0)
	 * @see #totalWorkProgressUpdate(ArmaAddonIndexingHandle, double)
	 */
	void currentWorkProgressUpdate(@NotNull ArmaAddonIndexingHandle handle, double progress);

	/**
	 * Invoked when a non-error and non-warning message is being passed for an addon.
	 * This is invoked while the addon is being indexed.
	 *
	 * @param handle  the addon's handle
	 * @param message the message
	 */
	void message(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message);

	/**
	 * Invoked when only an error message is being passed for an addon.
	 * This is invoked while the addon is being indexed.
	 *
	 * @param handle  the addon's handle
	 * @param message the message
	 */
	void errorMessage(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message, @Nullable Exception e);

	/**
	 * Invoked when only a warning message is being passed for an addon.
	 * This is invoked while the addon is being indexed.
	 *
	 * @param handle  the addon's handle
	 * @param message the message
	 */
	void warningMessage(@NotNull ArmaAddonIndexingHandle handle, @NotNull String message, @Nullable Exception e);

	/**
	 * Invoked when the indexing of an addon has reached and started a new step (this step may not ever be finished)
	 *
	 * @param handle  the addon's handle
	 * @param newStep the new step
	 */
	void stepStart(@NotNull ArmaAddonIndexingHandle handle, @NotNull Step newStep);

	/**
	 * Invoked when the indexing of an addon has finished a step
	 *
	 * @param handle       the addon's handle
	 * @param stepFinished the new step
	 */
	void stepFinish(@NotNull ArmaAddonIndexingHandle handle, @NotNull Step stepFinished);


	/**
	 * Invoked when the addon is completed indexed and the total work
	 * ({@link ArmaAddonIndexingHandle#getTotalWorkProgress()}) is 100% (or 1.0 in decimal)
	 *
	 * @param handle the addon's handle
	 */
	void indexFinishedForAddon(@NotNull ArmaAddonIndexingHandle handle);

	/**
	 * Invoked when all addons have been indexed. This method is invoked only once.
	 */
	void finishedIndex();

	/**
	 * Invoked when addons indexing has been started. This method is invoked only once.
	 *
	 * @param data the data used at the start of the index
	 */
	void startedIndex(@NotNull ArmaAddonsIndexingData data);
}
