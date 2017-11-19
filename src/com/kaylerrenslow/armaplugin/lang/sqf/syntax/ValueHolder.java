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
	 * ({@link ValueType#isArray}), this method will compare {@link ExpandedValueType} instances.
	 * <p>
	 * For comparing {@link ExpandedValueType} instances, the given type's {@link ExpandedValueType} must have >= number
	 * of elements to the allowed type's number of elements. Also, each element type must match at each index. If an array
	 * type is in the array type, this comparison will be used recursively.
	 *
	 * @return true if {@link #getAllAllowedTypes()}.contains(type) or if an allowed type and the provided type are both arrays.
	 */
	default boolean allowedTypesContains(@NotNull ValueType type) {
		ExpandedValueType typeExpanded = type.getExpanded();
		for (ValueType allowedType : getAllAllowedTypes()) {
			if (allowedType == type) {
				return true;
			}
			if (allowedType.isArray() && type.isArray()) {
				ExpandedValueType allowedExpanded = allowedType.getExpanded();

				LinkedList<ValueType> stackAllowed = new LinkedList<>();
				LinkedList<ValueType> stackProvided = new LinkedList<>();
				for (ValueType t : allowedExpanded.getValueTypes()) {
					stackAllowed.push(t);
				}
				for (ValueType t : typeExpanded.getValueTypes()) {
					stackProvided.push(t);
				}

				while (!stackAllowed.isEmpty()) {
					ValueType allowedTypePop = stackAllowed.pop();
					ValueType providedTypePop = stackProvided.pop();
					if (allowedTypePop.isArray()) {
						if (!providedTypePop.isArray()) {
							return false;
						}
						for (ValueType expandedElementType : allowedTypePop.getExpanded().getValueTypes()) {
							stackAllowed.push(expandedElementType);
						}
						for (ValueType expandedElementType : providedTypePop.getExpanded().getValueTypes()) {
							stackAllowed.push(expandedElementType);
						}
					} else {
						if (providedTypePop.isArray()) {
							return false;
						}
						if (!allowedTypePop.equals(providedTypePop)) {
							return false;
						}
					}
				}

				return true;
			}
		}
		return false;
	}
}
