package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 09/19/2017
 */
public class SQFWhileLoopHelperStatement implements SQFControlStructure, SQFLoopStatement {

	@NotNull
	private final SQFBlockOrExpression whileCondition;
	@NotNull
	private final SQFBlockOrExpression whileBody;

	public SQFWhileLoopHelperStatement(@NotNull SQFBlockOrExpression whileCondition, @NotNull SQFBlockOrExpression whileBody) {
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

	@NotNull
	@Override
	public SQFScope getImplicitPrivateScope() {
		if (whileBody.getBlock() != null && whileBody.getBlock().getScope() != null) {
			return whileBody.getBlock().getScope();
		}
		return null;
	}

	@NotNull
	@Override
	public List<SQFScope> getMergeScopes() {
		return Collections.emptyList();
	}
}
