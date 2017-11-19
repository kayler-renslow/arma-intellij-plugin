package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
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
	@NotNull
	private final List<Boolean> isRequired;
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
	 * Every value type provided will be marked as required.
	 *
	 * @param isUnbounded true if the last element in valueTypes is repeating, false otherwise
	 * @param valueTypes  value types to use
	 */
	public ExpandedValueType(boolean isUnbounded, @NotNull ValueType... valueTypes) {
		this.isUnbounded = isUnbounded;

		this.valueTypes = new ArrayList<>(valueTypes.length);
		Collections.addAll(this.valueTypes, valueTypes);

		this.isRequired = new ArrayList<>();
		for (ValueType t : valueTypes) {
			isRequired.add(true);
		}
	}

	/**
	 * Create an instance with the specified {@link ValueType} instances. By passing in an array <code>isRequired</code>,
	 * you can mark which indexes of <code>valueTypes</code> are required and not
	 *
	 * @param isUnbounded true if the last element in valueTypes is repeating, false otherwise
	 * @param valueTypes  value types to use
	 * @param isRequired  array of boolean dictating what elements in valueTypes are required and not required
	 * @throws IllegalArgumentException when valueTypes.length != isRequired.length
	 */
	public ExpandedValueType(boolean isUnbounded, @NotNull ValueType[] valueTypes, @NotNull boolean[] isRequired) {
		if (isRequired.length != valueTypes.length) {
			throw new IllegalArgumentException("isRequired.length != valueTypes.length");
		}
		this.isUnbounded = isUnbounded;

		this.valueTypes = new ArrayList<>(valueTypes.length);
		Collections.addAll(this.valueTypes, valueTypes);

		this.isRequired = new ArrayList<>(isRequired.length);
		for (boolean b : isRequired) {
			this.isRequired.add(b);
		}
	}

	/**
	 * @return true if this type contains more than one {@link ValueType} or {@link #isUnbounded()} is true.
	 * Also returns true if the only element is {@link ValueType.Lookup#ARRAY}. Otherwise, returns false
	 */
	public boolean isArray() {
		return isUnbounded || valueTypes.size() > 1 || (!valueTypes.isEmpty() && valueTypes.get(0) == ValueType.Lookup.ARRAY);
	}

	/**
	 * Check if the given index is required or not.
	 *
	 * @return true if index is required, false if it isn't
	 * @throws IndexOutOfBoundsException when ind &lt;0 or ind &gt;= the number of elements
	 */
	public boolean isRequired(int ind) {
		if (ind < 0 || ind >= isRequired.size()) {
			throw new IndexOutOfBoundsException();
		}
		return isRequired.get(ind);
	}

	/**
	 * @return a mutable list containing required indices
	 */
	@NotNull
	public List<Boolean> getIsRequired() {
		return isRequired;
	}

	/**
	 * @return mutable list containing value types
	 */
	@NotNull
	public List<ValueType> getValueTypes() {
		return valueTypes;
	}

	public void addValueType(@NotNull ValueType type, boolean isRequired) {
		valueTypes.add(type);
		this.isRequired.add(isRequired);
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

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof ExpandedValueType) {
			ExpandedValueType other = (ExpandedValueType) o;
			return this.getValueTypes().equals(other.getValueTypes())
					&& this.getIsRequired().equals(other.getIsRequired())
					&& this.isUnbounded == other.isUnbounded
					;
		}
		return false;
	}

	public String debugToString() {
		return "ExpandedValueType{" +
				"valueTypes=" + valueTypes +
				", isRequired=" + isRequired +
				", isUnbounded=" + isUnbounded +
				'}';
	}

	@Override
	public String toString() {
		return valueTypes.toString();
	}
}
