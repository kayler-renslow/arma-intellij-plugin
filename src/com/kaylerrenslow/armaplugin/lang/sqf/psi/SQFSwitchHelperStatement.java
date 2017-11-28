package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 09/18/2017
 */
public class SQFSwitchHelperStatement implements SQFControlStructure {
	@NotNull
	private final List<SQFCaseStatement> caseStatements;
	@Nullable
	private final SQFBlockOrExpression defaultStatementBlock;

	public SQFSwitchHelperStatement(@NotNull List<SQFCaseStatement> caseStatements, @Nullable SQFBlockOrExpression defaultStatementBlock) {
		this.caseStatements = caseStatements;
		this.defaultStatementBlock = defaultStatementBlock;
	}

	@NotNull
	public List<SQFCaseStatement> getCaseStatements() {
		return caseStatements;
	}

	@Nullable
	public SQFBlockOrExpression getDefaultStatementBlock() {
		return defaultStatementBlock;
	}

	@NotNull
	@Override
	public SQFScope getImplicitPrivateScope() {
		return null;
	}

	@NotNull
	@Override
	public List<SQFScope> getMergeScopes() {
		return Collections.emptyList();
	}
}
