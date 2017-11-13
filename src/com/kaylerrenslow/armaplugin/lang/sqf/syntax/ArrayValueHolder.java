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
		for (ValueHolder valueHolder : arrayDataValue.getValues()) {
			if (valueHolder instanceof ArrayValueHolder) {
				getArrayDataValueDisplayText((ArrayValueHolder) valueHolder, sb);
			} else {
				sb.append(valueHolder.getType().getDisplayName());
			}
			if (i != arrayDataValue.getValues().size() - 1) {
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
	List<? extends ValueHolder> getValues();
}
