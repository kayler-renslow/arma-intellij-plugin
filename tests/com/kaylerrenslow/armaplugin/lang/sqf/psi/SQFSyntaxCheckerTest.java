package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.InspectionManagerEx;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFFileType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 11/15/2017
 */
public class SQFSyntaxCheckerTest extends LightCodeInsightFixtureTestCase {

	public void testAddExpression_valid_number() throws Exception {
		//number plus number
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "1+1");
		ProblemsHolder problems = getProblemsHolder(file);
		SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

		assertEquals(0, problems.getResultCount());
	}

	public void testAddExpression_valid_string() throws Exception {
		//string plus string
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "''+''");
		ProblemsHolder problems = getProblemsHolder(file);
		SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

		assertEquals(0, problems.getResultCount());
	}

	public void testAddExpression_valid_array() throws Exception {
		//array plus array
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "[]+[]");
		ProblemsHolder problems = getProblemsHolder(file);
		SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

		assertEquals(0, problems.getResultCount());
	}

	public void testAddExpression_bad_numAndString() throws Exception {
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "1e1+''");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(1, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "''+1e1");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(1, problems.getResultCount());
		}
	}

	public void testAddExpression_bad_numAndArray() throws Exception {
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "0.5+[]");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(1, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "[]+0.5");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(1, problems.getResultCount());
		}
	}

	public void testAddExpression_bad_stringAndArray() throws Exception {
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "'hi'+[]");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(1, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "[]+'hello'");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(1, problems.getResultCount());
		}
	}

	@NotNull
	private ProblemsHolder getProblemsHolder(SQFFile file) {
		return new ProblemsHolder(new InspectionManagerEx(myFixture.getProject()), file, false);
	}

	private String getTestsFile(String fileName) {
		return "tests/com/kaylerrenslow/armaplugin/lang/sqf/psi/" + fileName;
	}
}
