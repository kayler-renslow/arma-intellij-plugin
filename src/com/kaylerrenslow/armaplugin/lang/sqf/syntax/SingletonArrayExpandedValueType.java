package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * A type of {@link ExpandedValueType} where there is only 1 {@link ValueType}, {@link #isArray()} will always return true,
 * and {@link #isUnbounded()} will always return false. Also, {@link #getNumOptionalValues()} will return 0 and
 * {@link #setNumOptionalValues(int)} will throw exception.
 *
 * @author Kayler
 * @since 11/21/2017
 */
public class SingletonArrayExpandedValueType extends ExpandedValueType {
	public SingletonArrayExpandedValueType(@NotNull ValueType valueType) {
		super(false, Collections.singletonList(valueType));
	}

	/**
	 * @return always true
	 */
	@Override
	public final boolean isArray() {
		return true;
	}

	/**
	 * @return an immutable list that contains only 1 {@link ValueType}
	 */
	@NotNull
	@Override
	public final List<ValueType> getValueTypes() {
		return super.getValueTypes();
	}

	/**
	 * @return always false
	 */
	@Override
	public final boolean isEmptyArray() {
		return false;
	}

	/**
	 * @return always false
	 */
	@Override
	public final boolean isUnbounded() {
		return false;
	}

	@NotNull
	@Override
	public final ExpandedValueType getExpanded() {
		return this;
	}

	@Override
	public final void addValueType(@NotNull ValueType type) {
		throw new IllegalStateException("can't add type to singleton array");
	}

	@Override
	public final int getNumOptionalValues() {
		return 0;
	}

	@Override
	public final void setNumOptionalValues(int numOptionalValues) {
		throw new IllegalStateException("can't set number of optional values for singleton array");
	}
}
