package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.codeInspection.ProblemsHolder;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptor;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.ValueType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tests for non-{@link SQFCommandExpression} syntax/type checking
 *
 * @author Kayler
 * @since 11/15/2017
 */
public class SQFSyntaxCheckerTest extends SQFSyntaxCheckerTestHelper {

	//----START Literal Expression----
	public void testLiteralExpression_number() throws Exception {
		ValueType ret = getExitTypeForText("1");
		assertEquals(ret, ValueType.NUMBER);
	}

	public void testLiteralExpression_string() throws Exception {
		ValueType ret = getExitTypeForText("'hello'");
		assertEquals(ret, ValueType.STRING);
	}

	public void testLiteralExpression_array() throws Exception {
		ValueType ret = getExitTypeForText("[1,2,3]");
		assertEquals(ret, ValueType.ARRAY);
	}

	//----END Literal Expression----

	//----START Paren Expression----

	public void testParenExpression1() throws Exception {
		ValueType ret = getExitTypeForText("(1)");
		assertEquals(ret, ValueType.NUMBER);
	}

	public void testParenExpression2() throws Exception {
		ValueType ret = getExitTypeForText("('hello')");
		assertEquals(ret, ValueType.STRING);
	}

	public void testParenExpression3() throws Exception {
		ValueType ret = getExitTypeForText("([1,2,3])");
		assertEquals(ret, ValueType.ARRAY);
	}

	public void testParenExpression4() throws Exception {
		ValueType ret = getExitTypeForText("(1+1)");
		assertEquals(ret, ValueType.NUMBER);
	}

	//----END Paren Expression----

	//----START Add Expression----

	public void testAddExpression_valid_number() throws Exception {
		assertNoProblems("1+1");
	}

	public void testAddExpression_valid_string() throws Exception {
		assertNoProblems("''+''");
	}

	public void testAddExpression_valid_array() throws Exception {
		assertNoProblems("[]+[]");
	}

	public void testAddExpression_valid_variable() throws Exception {
		assertNoProblems("_var+_var");
		assertNoProblems("1+_var");
		assertNoProblems("[]+_var");
		assertNoProblems("''+_var");
		assertNoProblems("_var+1");
		assertNoProblems("_var+[]");
		assertNoProblems("_var+''");
	}

	public void testAddExpression_bad_numAndString() throws Exception {
		assertProblemCount("1e1+''", 1);
		assertProblemCount("''+1e1", 1);
	}

	public void testAddExpression_bad_numAndArray() throws Exception {
		assertProblemCount("0.5+[]", 1);
		assertProblemCount("[]+0.5", 1);
	}

	public void testAddExpression_bad_stringAndArray() throws Exception {
		assertProblemCount("'hi'+[]", 1);
		assertProblemCount("[]+'hello'", 1);
	}

	//----END Add Expression----

	//----START Sub Expression----

	public void testSubExpression_valid_number() throws Exception {
		assertNoProblems("1-1");
	}

	public void testSubExpression_valid_array() throws Exception {
		assertNoProblems("[]-[]");
	}

	public void testSubExpression_valid_variable() throws Exception {
		assertNoProblems("_var-_var");
		assertNoProblems("1-_var");
		assertNoProblems("[]-_var");
		assertNoProblems("_var-1");
		assertNoProblems("_var-[]");
	}

	public void testSubExpression_bad_numAndString() throws Exception {
		assertProblemCount("1e1-''", 1);
		assertProblemCount("''-1e1", 1);
	}

	public void testSubExpression_bad_numAndArray() throws Exception {
		assertProblemCount("0.5-[]", 1);
		assertProblemCount("[]-0.5", 1);
	}

	//----END Sub Expression----

	//----START Mult Expression----
	public void testMultExpression_valid() throws Exception {
		assertNoProblems("0*1.5");
	}

	public void testMultExpression_bad() throws Exception {
		assertProblemCount("0*[]", 1);
		assertProblemCount("[]*0", 1);
	}

	public void testMultExpression_valid_variable() throws Exception {
		assertNoProblems("_var*_var");
		assertNoProblems("1*_var");
		assertNoProblems("_var*1");
	}
	//----END Mult Expression----

	//----START Mod Expression----
	public void testModExpression_valid() throws Exception {
		assertNoProblems("0%1.5");
	}

	public void testModExpression_bad() throws Exception {
		assertProblemCount("0%[]", 1);
		assertProblemCount("[]%0", 1);
	}

	public void testModExpression_valid_variable() throws Exception {
		assertNoProblems("_var%_var");
		assertNoProblems("1%_var");
		assertNoProblems("_var%1");
	}
	//----END Mod Expression----

	//----START Div Expression----
	public void testDivExpression_valid_number() throws Exception {
		assertNoProblems("0/1.5");
	}

	public void testDivExpression_valid_config() throws Exception {
		assertNoProblems("configFile/'CfgVehicles'");
		assertNoProblems("_var/'test'");
	}

	public void testDivExpression_bad_config() throws Exception {
		assertProblemCount("'test'/_var", 1);
		assertProblemCount("'test'/configFile", 1);
		assertProblemCount("0/configFile", 1);
		assertProblemCount("configFile/0", 1);
	}

	public void testDivExpression_bad_number() throws Exception {
		assertProblemCount("0/[]", 1);
		assertProblemCount("[]/0", 1);
	}

	public void testDivExpression_valid_number_variable() throws Exception {
		assertNoProblems("_var/_var");
		assertNoProblems("1/_var");
		assertNoProblems("_var/1");
	}

	//----END Div Expression----

	@Nullable
	private ValueType getExitTypeForText(@NotNull String text, @NotNull CommandDescriptor... descriptors) {
		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, text);
		ProblemsHolder problems = getProblemsHolder(file);
		return new SQFSyntaxChecker(
				file.getFileScope().getChildStatements(),
				new CommandDescriptorCluster(descriptors),
				problems
		).begin();
	}


}
