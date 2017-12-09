package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ValueType} that wraps a {@link ValueType} instance, but also has a mutable {@link ValueType#getPolymorphicTypes()}
 * that is shared by reference with its {@link #getExpanded()}
 *
 * @author Kayler
 * @since 12/08/2017
 */
public class PolymorphicWrapperValueType extends ValueType {
	@NotNull
	private final ValueType valueType;
	private final List<ValueType> polymorphicTypes = new ArrayList<>();
	private final ExpandedValueType expandedValueType;

	public PolymorphicWrapperValueType(@NotNull ValueType valueType) {
		this.valueType = valueType;
		expandedValueType = new ExpandedValueType(valueType.getExpanded(), polymorphicTypes);
	}

	@NotNull
	public ValueType getWrappedValueType() {
		return valueType;
	}

	@NotNull
	@Override
	public String getDisplayName() {
		return valueType.getDisplayName();
	}

	@Override
	public boolean isArray() {
		return valueType.isArray();
	}

	@NotNull
	@Override
	public ExpandedValueType getExpanded() {
		return expandedValueType;
	}

	@NotNull
	@Override
	public List<ValueType> getPolymorphicTypes() {
		return polymorphicTypes;
	}

	@NotNull
	@Override
	public String getType() {
		return valueType.getType();
	}

	@Override
	public String toString() {
		return valueType.getDisplayName();
	}

	@Override
	public boolean isHardEqual(@NotNull ValueType other) {
		return valueType.isHardEqual(other) || polymorphicTypes.contains(other);
	}
}
