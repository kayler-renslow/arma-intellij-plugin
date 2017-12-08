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
public class PolymorphicWrapperValueType implements ValueType {
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

	@Override
	public String toString() {
		return valueType.getDisplayName();
	}

	/**
	 * @return true if obj == this or obj is an {@link ValueType} and it Object.equals() {@link #getWrappedValueType()}
	 * or {@link #getPolymorphicTypes()} contains obj
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ValueType) {
			ValueType other = (ValueType) obj;
			return this.valueType.equals(other) || this.polymorphicTypes.contains(other);
		}
		return false;
	}
}
