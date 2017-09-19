package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author Kayler
 * @since 09/18/2017
 */
public class SQFIfStatement implements SQFControlStructure {
	@NotNull
	private final SQFExpression conditionExpr;
	@NotNull
	private final SQFCodeBlock thenBlock;
	@Nullable
	private final SQFCodeBlock elseBlock;

	public SQFIfStatement(@NotNull SQFExpression conditionExpr, @NotNull SQFCodeBlock thenBlock, @Nullable SQFCodeBlock elseBlock) {
		this.conditionExpr = conditionExpr;
		this.thenBlock = thenBlock;
		this.elseBlock = elseBlock;
	}

	@NotNull
	public SQFExpression getConditionExpr() {
		return conditionExpr;
	}

	@NotNull
	public SQFCodeBlock getThenBlock() {
		return thenBlock;
	}

	@Nullable
	public SQFCodeBlock getElseBlock() {
		return elseBlock;
	}
}
