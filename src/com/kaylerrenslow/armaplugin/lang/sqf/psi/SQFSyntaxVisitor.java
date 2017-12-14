package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 11/14/2017
 */
interface SQFSyntaxVisitor<T> {
	@NotNull
	T visit(@NotNull SQFScope scope, @NotNull CommandDescriptorCluster cluster);

	@NotNull
	T visit(@NotNull SQFAssignmentStatement statement, @NotNull CommandDescriptorCluster cluster);

	@NotNull
	T visit(@NotNull SQFCaseStatement statement, @NotNull CommandDescriptorCluster cluster);

	@NotNull
	T visit(@NotNull SQFExpressionStatement statement, @NotNull CommandDescriptorCluster cluster);

	@NotNull
	T visit(@NotNull SQFQuestStatement statement, @NotNull CommandDescriptorCluster cluster);

	@NotNull
	T visit(@NotNull SQFLiteralExpression expr, @NotNull CommandDescriptorCluster cluster);

	@NotNull
	T visit(@NotNull SQFParenExpression expr, @NotNull CommandDescriptorCluster cluster);

	@NotNull
	T visit(@NotNull SQFCommandExpression expr, @NotNull CommandDescriptorCluster cluster);

	@NotNull
	T visit(@NotNull SQFCodeBlockExpression expr, @NotNull CommandDescriptorCluster cluster);
}
