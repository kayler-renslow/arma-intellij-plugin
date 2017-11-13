package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @since 03/08/2017
 */
public interface ValueHolder {
	/**
	 * @return the {@link ValueHolderType} of the value
	 */
	@NotNull
	ValueHolderType getType();

	/**
	 * @return a description of the value
	 */
	@NotNull
	String getDescription();

	/**
	 * @return true if the value is optional, false otherwise
	 */
	boolean isOptional();

	/**
	 * @return a list of literals for the value, or an empty list for no literals
	 */
	@NotNull
	List<String> getLiterals();

	/**
	 * @return a list of {@link ValueHolderType} that this holder can have, or empty list if {@link #getType()} is only one
	 */
	@NotNull
	List<ValueHolderType> getAlternateValueTypes();
}
