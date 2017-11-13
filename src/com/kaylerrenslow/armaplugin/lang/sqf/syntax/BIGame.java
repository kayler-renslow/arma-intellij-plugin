package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 11/12/2017
 */
public class BIGame implements Comparable<BIGame> {
	private final String fullName;
	private final String shortName;
	private final String linkPrefix;

	public BIGame(@NotNull String fullName, @NotNull String shortName, @NotNull String linkPrefix) {
		this.fullName = fullName;
		this.shortName = shortName;
		this.linkPrefix = linkPrefix;
	}

	@NotNull
	public String getFullName() {
		return fullName;
	}

	@NotNull
	public String getShortName() {
		return shortName;
	}

	@NotNull
	public String getLinkPrefix() {
		return linkPrefix;
	}

	@Override
	public String toString() {
		return fullName;
	}

	@Override
	public int compareTo(@NotNull BIGame o) {
		return fullName.compareTo(o.fullName);
	}
}
