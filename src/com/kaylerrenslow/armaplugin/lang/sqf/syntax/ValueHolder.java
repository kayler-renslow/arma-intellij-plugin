package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @since 03/08/2017
 */
public interface ValueHolder {
	/**
	 * @return the {@link ValueType} of the value
	 */
	@NotNull
	ValueType getType();

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
	 * @return true if {@link ValueType#typeEquivalent(ValueType, ValueType)} returns true with {@link #getType()} as
	 * first argument and type as second argument
	 */
	default boolean containsType(@NotNull ValueType type) {
		return ValueType.typeEquivalent(getType(), type);
	}

}
