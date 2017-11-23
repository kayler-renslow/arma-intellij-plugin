package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @since 02/24/2017
 */
public class ArrayReturnValueHolder extends ReturnValueHolder implements ArrayValueHolder {
	private List<ReturnValueHolder> values;
	private boolean unbounded;

	public ArrayReturnValueHolder(@NotNull String description, @NotNull List<ReturnValueHolder> values, boolean unbounded) {
		super(ValueType.Lookup.ARRAY, description);
		this.values = values;
		this.unbounded = unbounded;
	}

	public boolean hasUnboundedParams() {
		return unbounded;
	}

	@NotNull
	public List<ReturnValueHolder> getValueHolders() {
		return values;
	}

	@NotNull
	@Override
	public ValueType getType() {
		return ArrayValueHolder.createType(this);
	}
}
