package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A way of expanding {@link ValueType} instances from something like {@link ValueType.Lookup#COLOR_RGB}
 * to {NUMBER, NUMBER, NUMBER}
 *
 * @author Kayler
 * @since 11/18/2017
 */
public class ExpandedValueType implements ValueType {
	@NotNull
	private final List<ValueType> valueTypes;
	private final boolean isUnbounded;

	/**
	 * Create an instance with the specified <b>required</b> {@link ValueType} instances.
	 * Every value type provided will be marked as required.
	 * <p>
	 * This will invoke {@link ExpandedValueType#ExpandedValueType(boolean, ValueType...)} with unbounded set to false
	 *
	 * @param valueTypes value types to use
	 */
	public ExpandedValueType(@NotNull ValueType... valueTypes) {
		this(false, valueTypes);
	}

	/**
	 * Create an instance with the specified <b>required</b> {@link ValueType} instances.
	 *
	 * @param isUnbounded true if the last element in valueTypes is repeating, false otherwise
	 * @param valueTypes  value types to use
	 */
	public ExpandedValueType(boolean isUnbounded, @NotNull ValueType... valueTypes) {
		this.isUnbounded = isUnbounded;

		this.valueTypes = new ArrayList<>(valueTypes.length);
		Collections.addAll(this.valueTypes, valueTypes);
	}

	/**
	 * @return true if this type contains more than one {@link ValueType} or {@link #isUnbounded()} is true.
	 * Also returns true if the only element is {@link ValueType.Lookup#ARRAY}. Otherwise, returns false
	 */
	public boolean isArray() {
		return isUnbounded || valueTypes.size() > 1 || (!valueTypes.isEmpty() && valueTypes.get(0) == ValueType.Lookup.ARRAY);
	}

	/**
	 * @return mutable list containing value types
	 */
	@NotNull
	public List<ValueType> getValueTypes() {
		return valueTypes;
	}

	public void addValueType(@NotNull ValueType type) {
		valueTypes.add(type);
	}

	@NotNull
	@Override
	public String getDisplayName() {
		return valueTypes.size() == 1 ? valueTypes.get(0).getDisplayName() : valueTypes.toString();
	}

	/**
	 * @return this
	 */
	@NotNull
	@Override
	public ExpandedValueType getExpanded() {
		return this;
	}

	/**
	 * @return true if the last element in {@link #getValueTypes()} is repeating, false otherwise
	 */
	public boolean isUnbounded() {
		return isUnbounded;
	}

	/**
	 * @return true if {@link #getValueTypes()} are equal (same size and order) and if {@link #isUnbounded()} are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof ExpandedValueType) {
			ExpandedValueType other = (ExpandedValueType) o;

			if (getValueTypes().size() != other.getValueTypes().size()) {
				return false;
			}

			Iterator<ValueType> myTypes = getValueTypes().iterator();
			Iterator<ValueType> otherTypes = other.getValueTypes().iterator();
			while (myTypes.hasNext()) {
				if (!myTypes.next().equals(otherTypes.next())) {
					return false;
				}
			}
			return this.isUnbounded == other.isUnbounded;
		}
		return false;
	}

	public String debugToString() {
		return "ExpandedValueType{" +
				"valueTypes=" + valueTypes +
				", isUnbounded=" + isUnbounded +
				'}';
	}

	@Override
	public String toString() {
		return valueTypes.toString();
	}
}
