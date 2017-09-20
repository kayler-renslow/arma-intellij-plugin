package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Kayler
 * @since 09/18/2017
 */
public class SQFSwitchStatement implements SQFControlStructure {
	@NotNull
	private final List<SQFCaseStatement> caseStatements;
	@Nullable
	private final SQFBlockOrExpression defaultStatementBlock;

	public SQFSwitchStatement(@NotNull List<SQFCaseStatement> caseStatements, @Nullable SQFBlockOrExpression defaultStatementBlock) {
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
}
