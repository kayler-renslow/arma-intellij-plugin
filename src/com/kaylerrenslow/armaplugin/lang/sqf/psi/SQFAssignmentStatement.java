package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFAssignmentStatement implements SQFStatement {
	@NotNull
	public SQFVariable getVariable() {
		return null;
	}

	@NotNull
	public SQFExpression getExpression() {
		return null;
	}
}
