package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFBoolAndExpression implements SQFExpression, SQFBinaryExpression {
	@NotNull
	@Override
	public SQFExpression getLeft() {
		return null;
	}

	@NotNull
	@Override
	public SQFExpression getRight() {
		return null;
	}
}
