package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 09/19/2017
 */
public class SQFWhileLoopStatement implements SQFControlStructure, SQFLoopStatement {

	@NotNull
	private final SQFBlockOrExpression whileCondition;
	@NotNull
	private final SQFBlockOrExpression whileBody;

	public SQFWhileLoopStatement(@NotNull SQFBlockOrExpression whileCondition, @NotNull SQFBlockOrExpression whileBody) {
		this.whileCondition = whileCondition;
		this.whileBody = whileBody;
	}

	@NotNull
	public SQFBlockOrExpression getWhileCondition() {
		return whileCondition;
	}

	@NotNull
	public SQFBlockOrExpression getWhileBody() {
		return whileBody;
	}
}
