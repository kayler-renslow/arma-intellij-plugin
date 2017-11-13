package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 02/24/2017
 */
public class ReturnValueHolder extends BasicValueHolder {
	public ReturnValueHolder(@NotNull ValueHolderType type, @NotNull String description) {
		super(type, description, false);
	}

	/**
	 * @return always false
	 */
	@Override
	public boolean isOptional() {
		return false;
	}
}
