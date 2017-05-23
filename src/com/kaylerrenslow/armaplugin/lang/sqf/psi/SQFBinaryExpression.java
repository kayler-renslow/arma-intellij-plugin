package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public interface SQFBinaryExpression {
	@NotNull
	SQFExpression getLeft();

	@NotNull
	SQFExpression getRight();
}
