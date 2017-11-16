package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.InspectionManagerEx;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.ValueType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Contains helper methods for {@link SQFSyntaxChecker} related tests
 *
 * @author Kayler
 * @since 11/16/2017
 */
public abstract class SQFSyntaxCheckerTestHelper extends LightCodeInsightFixtureTestCase {

	/**
	 * Parses the given text as SQF into a {@link SQFFile} and then runs
	 * {@link SQFSyntaxHelper#checkSyntax(SQFFile, ProblemsHolder)} on it.
	 * <p>
	 * Asserts that the problems detected are equal to the ones provided.
	 *
	 * @param text             the SQF code to parse into a {@link SQFFile}
	 * @param expectedProblems number of problems to expect
	 * @see #assertNoProblems(String)
	 */
	public void assertProblemCount(@NotNull String text, int expectedProblems) {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, text);
		ProblemsHolder problems = getProblemsHolder(file);
		SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

		assertEquals(expectedProblems, problems.getResultCount());
	}

	/**
	 * Parses the given text as SQF into a {@link SQFFile} and then runs
	 * {@link SQFSyntaxHelper#checkSyntax(SQFFile, ProblemsHolder)} on it.
	 * <p>
	 * Asserts that the problems detected are equal to 0.
	 *
	 * @param text the SQF code to parse into a {@link SQFFile}
	 * @see #assertNoProblems(String)
	 */
	public void assertNoProblems(@NotNull String text) {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, text);
		ProblemsHolder problems = getProblemsHolder(file);
		SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

		assertEquals(0, problems.getResultCount());
	}

	/**
	 * Parses the given text into a {@link SQFFile}. Then it creates a {@link SQFSyntaxChecker} and runs
	 * it on the entire file and returns the last {@link ValueType} of all the statements.
	 *
	 * @param text SQF code to parse
	 * @return the {@link SQFSyntaxChecker#begin()} exit value
	 */
	@Nullable
	public ValueType getExitTypeForText(@NotNull String text) {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, text);
		ProblemsHolder problems = getProblemsHolder(file);
		CommandDescriptorCluster cluster = SQFSyntaxHelper.getInstance().getCommandDescriptors(file.getNode());
		return new SQFSyntaxChecker(file.getFileScope().getChildStatements(), cluster, problems).begin();
	}

	/**
	 * @return a {@link ProblemsHolder} instance for the given {@link SQFFile}
	 */
	@NotNull
	public ProblemsHolder getProblemsHolder(@NotNull SQFFile file) {
		return new ProblemsHolder(new InspectionManagerEx(myFixture.getProject()), file, false);
	}
}
