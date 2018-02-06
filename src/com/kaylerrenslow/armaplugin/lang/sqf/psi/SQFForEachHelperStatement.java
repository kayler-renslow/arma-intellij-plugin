package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 02/04/2018
 */
public class SQFForEachHelperStatement implements SQFControlStructure {
	@NotNull
	private final SQFBlockOrExpression code;
	@NotNull
	private final SQFExpression iterableExpression;

	public SQFForEachHelperStatement(@NotNull SQFBlockOrExpression code, @NotNull SQFExpression iterableExpression) {
		this.code = code;
		this.iterableExpression = iterableExpression;
	}

	/**
	 * @return the {} part of "{} forEach []"
	 */
	@NotNull
	public SQFBlockOrExpression getCode() {
		return code;
	}

	/**
	 * Note that SQF expects forEach to be passed an array, but the parser in the plugin will allow any expression, which is why
	 * we are returning that here
	 *
	 * @return the [] part of "{} forEach []"
	 */
	@NotNull
	public SQFExpression getIterableExpression() {
		return iterableExpression;
	}
}
