package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.InspectionManagerEx;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.ValueType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 11/15/2017
 */
public class SQFSyntaxCheckerTest extends LightCodeInsightFixtureTestCase {

	//----START Literal Expression----
	public void testLiteralExpression_number() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "1");
		ProblemsHolder problems = getProblemsHolder(file);
		ValueType ret = new SQFSyntaxChecker(
				file.getFileScope().getChildStatements(),
				new CommandDescriptorCluster(),
				problems
		).begin();

		assertEquals(ret, ValueType.NUMBER);
	}

	public void testLiteralExpression_string() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "'hello'");
		ProblemsHolder problems = getProblemsHolder(file);
		ValueType ret = new SQFSyntaxChecker(
				file.getFileScope().getChildStatements(),
				new CommandDescriptorCluster(),
				problems
		).begin();

		assertEquals(ret, ValueType.STRING);
	}

	public void testLiteralExpression_array() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "[1,2,3]");
		ProblemsHolder problems = getProblemsHolder(file);
		ValueType ret = new SQFSyntaxChecker(
				file.getFileScope().getChildStatements(),
				new CommandDescriptorCluster(),
				problems
		).begin();

		assertEquals(ret, ValueType.ARRAY);
	}

	//----END Literal Expression----

	//----START Paren Expression----

	public void testParenExpression1() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "(1)");
		ProblemsHolder problems = getProblemsHolder(file);
		ValueType ret = new SQFSyntaxChecker(
				file.getFileScope().getChildStatements(),
				new CommandDescriptorCluster(),
				problems
		).begin();

		assertEquals(ret, ValueType.NUMBER);
	}

	public void testParenExpression2() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "('hello')");
		ProblemsHolder problems = getProblemsHolder(file);
		ValueType ret = new SQFSyntaxChecker(
				file.getFileScope().getChildStatements(),
				new CommandDescriptorCluster(),
				problems
		).begin();

		assertEquals(ret, ValueType.STRING);
	}

	public void testParenExpression3() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "([1,2,3])");
		ProblemsHolder problems = getProblemsHolder(file);
		ValueType ret = new SQFSyntaxChecker(
				file.getFileScope().getChildStatements(),
				new CommandDescriptorCluster(),
				problems
		).begin();

		assertEquals(ret, ValueType.ARRAY);
	}

	public void testParenExpression4() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "(1+1)");
		ProblemsHolder problems = getProblemsHolder(file);
		ValueType ret = new SQFSyntaxChecker(
				file.getFileScope().getChildStatements(),
				new CommandDescriptorCluster(),
				problems
		).begin();

		assertEquals(ret, ValueType.NUMBER);
	}

	//----END Paren Expression----

	//----START Add Expression----

	public void testAddExpression_valid_number() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "1+1");
		ProblemsHolder problems = getProblemsHolder(file);
		SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

		assertEquals(0, problems.getResultCount());
	}

	public void testAddExpression_valid_string() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "''+''");
		ProblemsHolder problems = getProblemsHolder(file);
		SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

		assertEquals(0, problems.getResultCount());
	}

	public void testAddExpression_valid_array() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "[]+[]");
		ProblemsHolder problems = getProblemsHolder(file);
		SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

		assertEquals(0, problems.getResultCount());
	}

	public void testAddExpression_valid_variable() throws Exception {
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "_var+_var");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(0, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "1+_var");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(0, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "[]+_var");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(0, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "''+_var");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(0, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "_var+1");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(0, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "_var+[]");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(0, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "_var+''");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(0, problems.getResultCount());
		}
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

	//----END Add Expression----

	//----START Sub Expression----

	public void testSubExpression_valid_number() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "1-1");
		ProblemsHolder problems = getProblemsHolder(file);
		SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

		assertEquals(0, problems.getResultCount());
	}

	public void testSubExpression_valid_array() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "[]-[]");
		ProblemsHolder problems = getProblemsHolder(file);
		SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

		assertEquals(0, problems.getResultCount());
	}

	public void testSubExpression_valid_variable() throws Exception {
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "_var-_var");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(0, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "1-_var");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(0, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "[]-_var");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(0, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "_var-1");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(0, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "_var-[]");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(0, problems.getResultCount());
		}
	}

	public void testSubExpression_bad_numAndString() throws Exception {
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "1e1-''");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(1, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "''-1e1");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(1, problems.getResultCount());
		}
	}

	public void testSubExpression_bad_numAndArray() throws Exception {
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "0.5-[]");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(1, problems.getResultCount());
		}
		{
			SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, "[]-0.5");
			ProblemsHolder problems = getProblemsHolder(file);
			SQFSyntaxHelper.getInstance().checkSyntax(file, problems);

			assertEquals(1, problems.getResultCount());
		}
	}

	//----END Sub Expression----

	@NotNull
	private ProblemsHolder getProblemsHolder(SQFFile file) {
		return new ProblemsHolder(new InspectionManagerEx(myFixture.getProject()), file, false);
	}

}
