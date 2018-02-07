package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Kayler
 * @since 09/18/2017
 */
public class SQFSwitchHelperStatement extends SQFHelperStatement implements SQFControlStructure {
	@NotNull
	private final List<SQFCaseStatement> caseStatements;
	@Nullable
	private final SQFCommandExpression defaultCommandExpr;

	public SQFSwitchHelperStatement(@NotNull SQFStatement statement, @NotNull List<SQFCaseStatement> caseStatements, @Nullable SQFCommandExpression defaultCommandExpr) {
		super(statement);
		this.caseStatements = caseStatements;
		this.defaultCommandExpr = defaultCommandExpr;
	}

	@NotNull
	public List<SQFCaseStatement> getCaseStatements() {
		return caseStatements;
	}

	@Nullable
	public SQFCommandExpression getDefaultCommandExpr() {
		return defaultCommandExpr;
	}
}
