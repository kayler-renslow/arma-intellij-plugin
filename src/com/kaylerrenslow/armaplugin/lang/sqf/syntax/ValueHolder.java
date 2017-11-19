package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
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
	 * @return a list of {@link ValueType} that this holder can have, or empty list if {@link #getType()} is only one
	 */
	@NotNull
	List<ValueType> getAlternateValueTypes();

	/**
	 * @return a list that contains {@link #getType()} and {@link #getAlternateValueTypes()}
	 */
	@NotNull
	default List<ValueType> getAllAllowedTypes() {
		LinkedList<ValueType> types = new LinkedList<>();
		types.add(getType());
		types.addAll(getAlternateValueTypes());
		return types;
	}

	/**
	 * Checks if the given type is inside {@link #getAllAllowedTypes()}. If <code>type</code> is an array type
	 * ({@link ValueType#isArray}), this method will return true if a type in {@link #getAllAllowedTypes()} is also
	 * an array type.
	 *
	 * @return true if {@link #getAllAllowedTypes()}.contains(type) or if an allowed type and the provided type are both arrays.
	 */
	default boolean allowedTypesContains(@NotNull ValueType type) {
		for (ValueType allowedType : getAllAllowedTypes()) {
			if (allowedType == type) {
				return true;
			}
			if (allowedType.isArray() && type.isArray()) {
				return true;
			}
		}
		return false;
	}
}
