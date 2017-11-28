package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 09/18/2017
 */
public class SQFIfHelperStatement implements SQFControlStructure {
	@NotNull
	private final SQFExpression conditionExpr;
	@NotNull
	private final SQFBlockOrExpression thenBlock;
	@Nullable
	private final SQFBlockOrExpression elseBlock;

	public SQFIfHelperStatement(@NotNull SQFExpression conditionExpr, @NotNull SQFBlockOrExpression thenBlock, @Nullable SQFBlockOrExpression elseBlock) {
		this.conditionExpr = conditionExpr;
		this.thenBlock = thenBlock;
		this.elseBlock = elseBlock;
	}

	@NotNull
	public SQFExpression getCondition() {
		return conditionExpr;
	}

	@NotNull
	public SQFBlockOrExpression getThen() {
		return thenBlock;
	}

	@Nullable
	public SQFBlockOrExpression getElse() {
		return elseBlock;
	}

	@NotNull
	@Override
	public SQFScope getImplicitPrivateScope() {
		if (thenBlock.getBlock() != null && thenBlock.getBlock().getScope() != null) {
			return thenBlock.getBlock().getScope();
		}
		return null;
	}

	@NotNull
	@Override
	public List<SQFScope> getMergeScopes() {
		if (elseBlock != null && elseBlock.getBlock() != null && elseBlock.getBlock().getScope() != null) {
			return Collections.singletonList(elseBlock.getBlock().getScope());
		}
		return Collections.emptyList();
	}
}
