package com.kaylerrenslow.armaplugin;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @see ArmaAddonsManager
 * @see ArmaAddonsIndexingCallback
 * @since 10/28/2017
 */
public class ArmaAddonsIndexingData {
	@NotNull
	private final ArmaAddonsProjectConfig config;
	@NotNull
	private final List<String> addonsMarkedToIndex;

	public ArmaAddonsIndexingData(@NotNull ArmaAddonsProjectConfig config, @NotNull List<String> addonsMarkedToIndex) {
		this.config = config;
		this.addonsMarkedToIndex = addonsMarkedToIndex;
	}

	@NotNull
	public ArmaAddonsProjectConfig getConfig() {
		return config;
	}

	@NotNull
	public List<String> getAddonsMarkedToIndex() {
		return addonsMarkedToIndex;
	}
}
