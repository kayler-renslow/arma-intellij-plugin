package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

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
	 * Checks each {@link #getAllAllowedTypes()} against type.
	 * The comparing method used is {@link ValueType#typeEquivalent(ValueType, ValueType)}.
	 * For each allowed type in {@link #getAllAllowedTypes()}, it specifies the required minimum length for it to be equal to type.
	 * <p>
	 * If {@link #getAllAllowedTypes()} doesn't contain type, then this method will iterate index by index of {@link #getValueHolders()}
	 * as well as index of type's expanded type and check each {@link ValueHolder#getAllAllowedTypes()} against each index of type
	 *
	 * @return true if {@link #getAllAllowedTypes()} has 1 element that is equal to type, false otherwise
	 */


	/**
	 * @return {@link #createType(ArrayValueHolder)} of this
	 */
	@NotNull
	@Override
	default ValueType getType() {
		return ArrayValueHolder.createType(this);
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
