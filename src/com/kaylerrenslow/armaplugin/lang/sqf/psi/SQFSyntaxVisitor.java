package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * @since 11/14/2017
 */
interface SQFSyntaxVisitor<T> {
	@Nullable
	T visit(@NotNull SQFScope scope, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFAssignmentStatement statement, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFCaseStatement statement, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFExpressionStatement statement, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFQuestStatement statement, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFAddExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFSubExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFMultExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFDivExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFModExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFBoolAndExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFBoolOrExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFBoolNotExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFCompExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFConfigFetchExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFExponentExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFLiteralExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFParenExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFCommandExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFCodeBlockExpression expr, @NotNull CommandDescriptorCluster cluster);

	@Nullable
	T visit(@NotNull SQFSignedExpression expr, @NotNull CommandDescriptorCluster cluster);
}
