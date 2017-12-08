package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

/**
 * @author Kayler
 * @since 02/27/2017
 */
public interface ArrayValueHolder extends ValueHolder {
	@NotNull
	static String getArrayDataValueDisplayText(@NotNull ArrayValueHolder arrayDataValue, @NotNull StringBuilder sb) {
		sb.append("[");
		int i = 0;
		for (ValueHolder valueHolder : arrayDataValue.getValueHolders()) {
			if (valueHolder instanceof ArrayValueHolder) {
				getArrayDataValueDisplayText((ArrayValueHolder) valueHolder, sb);
			} else {
				sb.append(valueHolder.getType().getDisplayName());
			}
			if (i != arrayDataValue.getValueHolders().size() - 1) {
				sb.append(", ");
			}
			i++;
		}
		if (arrayDataValue.hasUnboundedParams()) {
			sb.append(" ...");
		}
		sb.append("]");
		return sb.toString();
	}

	boolean hasUnboundedParams();

	@NotNull
	List<? extends ValueHolder> getValueHolders();

	/**
	 * Invokes {@link ValueHolder#allowedTypesContains(ValueType)}. If it returns true, this returns true. If it returns false,
	 * then this method will iterate index by index of {@link #getValueHolders()}  as well as
	 * index of type's expanded type and check each {@link ValueHolder#getAllAllowedTypes()} against each index of type.
	 *
	 * @return true if type is in allowed types
	 */
	@Override
	default boolean allowedTypesContains(@NotNull ValueType type) {
		if (ValueHolder.super.allowedTypesContains(type)) {
			return true;
		}
		if (!type.isArray()) {
			return false;
		}
		//check index by index by also checking their allowed types
		Iterator<ValueType> typesIter = type.getExpanded().getValueTypes().iterator();
		for (ValueHolder h : getValueHolders()) {
			if (!typesIter.hasNext() && h.isOptional()) {
				//assuming remaining holders are optional, return true
				return true;
			}
			if (!typesIter.hasNext()) {
				return false;
			}
			if (!h.allowedTypesContains(typesIter.next())) {
				return false;
			}
		}
		return true;
	}

	@NotNull
	static ValueType createType(@NotNull ArrayValueHolder h) {
		return createType(h.getValueHolders(), h.hasUnboundedParams());
	}

	@NotNull
	static ValueType createType(@NotNull List<? extends ValueHolder> holders, boolean unbounded) {
		if (holders.size() == 1 && !unbounded) {
			return new SingletonArrayExpandedValueType(holders.get(0).getType());
		}
		ExpandedValueType t = new ExpandedValueType(unbounded);
		int numOptional = 0;
		for (ValueHolder childH : holders) {
			t.getValueTypes().add(childH.getType());
			numOptional += childH.isOptional() ? 1 : 0;
		}
		t.setNumOptionalValues(numOptional);
		return t;
	}
}
