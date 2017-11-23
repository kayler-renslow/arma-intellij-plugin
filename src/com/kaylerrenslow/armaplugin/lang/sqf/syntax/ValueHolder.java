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
	 * Checks each {@link #getAllAllowedTypes()} against type.
	 * The comparing method used is {@link ValueType#typeEquivalent(ValueType, ValueType)}.
	 * For each allowed type in {@link #getAllAllowedTypes()}, it specifies the required minimum length for it to be equal to type.
	 *
	 * @return true if {@link #getAllAllowedTypes()} has 1 element that is equal to type, false otherwise
	 */
	default boolean allowedTypesContains(@NotNull ValueType type) {
		for (ValueType allowedType : getAllAllowedTypes()) {
			if (ValueType.typeEquivalent(allowedType, type)) {
				return true;
			}
		}
		return false;
	}

}
