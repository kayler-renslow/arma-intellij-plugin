package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.kaylerrenslow.armaplugin.lang.sqf.syntax.ValueType;

/**
 * Tests for syntax/type checking for {@link SQFFile} instances
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

	//----START Bool And Expression----
	public void testBoolAndExpression_valid() throws Exception {
		assertNoProblems("true && true");
		assertNoProblems("true && false");
		assertNoProblems("false && {true}");
	}

	public void testBoolAndExpression_bad() throws Exception {
		assertProblemCount("true && 1", 1);
		assertProblemCount("true && []", 1);
		assertProblemCount("false && {5}", 1);
	}

	public void testBoolAndExpression_valid_variable() throws Exception {
		assertNoProblems("_var && _var");
		assertNoProblems("_var && {_var}");
		assertNoProblems("true && _var");
		assertNoProblems("true && {_var}");
	}

	//----END Bool And Expression----

	//----START Bool Or Expression----
	public void testBoolOrExpression_valid() throws Exception {
		assertNoProblems("true || true");
		assertNoProblems("true || false");
		assertNoProblems("false || {true}");
	}

	public void testBoolOrExpression_bad() throws Exception {
		assertProblemCount("true || 1", 1);
		assertProblemCount("true || []", 1);
		assertProblemCount("false || {5}", 1);
	}

	public void testBoolOrExpression_valid_variable() throws Exception {
		assertNoProblems("_var || _var");
		assertNoProblems("_var || {_var}");
		assertNoProblems("true || _var");
		assertNoProblems("true || {_var}");
	}
	//----END Bool Or Expression----

	//----START Bool Not Expression----
	public void testBoolNotExpression_valid() throws Exception {
		assertNoProblems("!true");
		assertNoProblems("!(true || false)");
		assertNoProblems("!(false || {true})");

		assertNoProblems("!_var");
	}

	public void testBoolNotExpression_bad() throws Exception {
		assertProblemCount("!1", 1);
		assertProblemCount("![]", 1);
		assertProblemCount("!{5}", 1);
		assertProblemCount("!{true}", 1);
	}
	//----END Bool Not Expression----

	//----START Comp Expression----
	public void testCompExpression_valid() throws Exception {
		assertNoProblems("1 < 1");
		assertNoProblems("1 < 0.5");

		assertNoProblems("1 <= 10");
		assertNoProblems("1 <= 0.5");

		assertNoProblems("1 > 1");
		assertNoProblems("1 > 0.5");

		assertNoProblems("1 >= 10");
		assertNoProblems("1 >= 0.5");

		assertNoProblems("1==1"); //number
		assertNoProblems("false == true"); //boolean
		assertNoProblems("''==''"); //string

		{ //group
			assertEquals(getExitTypeForText("groupNull"), ValueType.GROUP);
			assertNoProblems("groupNull==groupNull");
			assertNoProblems("groupNull!=groupNull");
		}

		{ //side
			assertEquals(getExitTypeForText("west"), ValueType.SIDE);
			assertNoProblems("west==west");
			assertNoProblems("west!=west");
		}

		{ //object
			assertEquals(getExitTypeForText("objectNull"), ValueType.OBJECT);
			assertNoProblems("objectNull==objectNull");
			assertNoProblems("objectNull!=objectNull");
		}

		{ //config
			assertEquals(getExitTypeForText("configFile"), ValueType.CONFIG);
			assertNoProblems("configFile==configFile");
			assertNoProblems("configFile!=configFile");
		}

		{ //display
			assertEquals(getExitTypeForText("displayNull"), ValueType.DISPLAY);
			assertNoProblems("displayNull==displayNull");
			assertNoProblems("displayNull!=displayNull");
		}

		{ //control
			assertEquals(getExitTypeForText("controlNull"), ValueType.CONTROL);
			assertNoProblems("controlNull==controlNull");
			assertNoProblems("controlNull!=controlNull");
		}

		{ //location
			assertEquals(getExitTypeForText("locationNull"), ValueType.LOCATION);
			assertNoProblems("locationNull==controlNull");
			assertNoProblems("locationNull!=controlNull");
		}

		{ //structured text
			assertEquals(getExitTypeForText("parseText ''"), ValueType.STRUCTURED_TEXT);
			assertNoProblems("(parseText '')==(parseText '')");
			assertNoProblems("(parseText '')!=(parseText '')");
		}

	}

	public void testCompExpression_bad() throws Exception {
		assertProblemCount("1 < ''", 1);
		assertProblemCount("1 < []", 1);
		assertProblemCount("'' < []", 2);
		assertProblemCount("[] < 1", 1);
		assertProblemCount("[] < []", 2);

		assertProblemCount("1 <= ''", 1);
		assertProblemCount("1 <= []", 1);
		assertProblemCount("'' <= []", 2);
		assertProblemCount("[] <= 1", 1);
		assertProblemCount("[] <= []", 2);

		assertProblemCount("1 > ''", 1);
		assertProblemCount("1 > []", 1);
		assertProblemCount("'' > []", 2);
		assertProblemCount("[] > 1", 1);
		assertProblemCount("[] > []", 2);

		assertProblemCount("1 >= ''", 1);
		assertProblemCount("1 >= []", 1);
		assertProblemCount("'' >= []", 2);
		assertProblemCount("[] >= 1", 1);
		assertProblemCount("[] >= []", 2);

		assertProblemCount("1 == ''", 1);
		assertProblemCount("[] == []", 2);
		assertProblemCount("1 == []", 1);
		assertProblemCount("'' == []", 2);
		assertProblemCount("[] == 1", 1);
		assertProblemCount("[] == false", 1);
		assertProblemCount("groupNull==west", 1);
		assertProblemCount("west==groupNull", 1);
		assertProblemCount("objectNull==1", 1);
		assertProblemCount("configFile==[]", 1);
		assertProblemCount("displayNull==''", 1);
		assertProblemCount("controlNull==[]", 1);
		assertProblemCount("locationNull==false", 1);
		assertProblemCount("(parseText '')==0", 1);

		assertProblemCount("[] != []", 2);
		assertProblemCount("1 != []", 1);
		assertProblemCount("'' != []", 2);
		assertProblemCount("[] != 1", 1);
		assertProblemCount("[] != false", 1);
		assertProblemCount("configFile!=[]", 1);
		assertProblemCount("controlNull!=[]", 1);
		assertProblemCount("1 != ''", 1);
		assertProblemCount("groupNull!=west", 1);
		assertProblemCount("west!=groupNull", 1);
		assertProblemCount("objectNull!=1", 1);
		assertProblemCount("displayNull!=''", 1);
		assertProblemCount("locationNull!=false", 1);
		assertProblemCount("(parseText '')!=0", 1);
	}

	public void testCompExpression_valid_variable() throws Exception {
		assertNoProblems("_var < _var");
		assertNoProblems("1 < _var");
		assertNoProblems("_var < 1");

		assertNoProblems("_var <= _var");
		assertNoProblems("1 <= _var");
		assertNoProblems("_var <= 1");

		assertNoProblems("_var > _var");
		assertNoProblems("1 > _var");
		assertNoProblems("_var > 1");

		assertNoProblems("_var >= _var");
		assertNoProblems("1 >= _var");
		assertNoProblems("_var >= 1");

		assertNoProblems("_var == _var");
		assertNoProblems("1 == _var");
		assertNoProblems("_var == 1");
		assertNoProblems("_var == configFile");
		assertNoProblems("_var == true");
		assertNoProblems("configFile == _var");
		assertNoProblems("true == _var");

		assertNoProblems("_var != _var");
		assertNoProblems("1 != _var");
		assertNoProblems("_var != 1");
		assertNoProblems("_var != configFile");
		assertNoProblems("_var != true");
		assertNoProblems("configFile != _var");
		assertNoProblems("true != _var");
	}
	//----END Comp Expression----

	//----START config fetch Expression----
	public void testConfigFetchExpression_valid() throws Exception {
		assertNoProblems("configFile >> ''");
		assertNoProblems("configFile >> '' >> ''");
		assertNoProblems("configFile >> '' >> '' >> ''");

		assertNoProblems("configFile >> _var >> '' >> _var");
		assertNoProblems("configFile >> _var");
		assertNoProblems("configFile >> '' >> _var >> _var");
		assertNoProblems("_var >> ''");
		assertNoProblems("_var >> _var");
	}

	public void testConfigFetchExpression_bad() throws Exception {
		assertProblemCount("configFile >> 1", 1);
		assertProblemCount("configFile >> 1 >> 2", 2);
		assertProblemCount("1 >> '' >> ''", 1);
		assertProblemCount("_var >> 1 >> ''", 1);
	}
	//----END config fetch Expression----

	//----START exponent Expression----
	public void testExponentExpression_valid() throws Exception {
		assertNoProblems("1^1");
		assertNoProblems("1^1^5");
		assertNoProblems("1^1^5^7");

		assertNoProblems("_var^1^5^7");
		assertNoProblems("_var^_var^5^7");
		assertNoProblems("_var^1^5^_var");
		assertNoProblems("1^1^_var^7");
		assertNoProblems("_var^_var^_var^_var");
	}

	public void testExponentExpression_bad() throws Exception {
		assertProblemCount("1^1^''", 1);
		assertProblemCount("''^1^5", 1);
		assertProblemCount("''^1^5^''", 2);

		assertProblemCount("_var^''^5^7", 1);
		assertProblemCount("''^_var^5^7", 1);
		assertProblemCount("_var^1^''^_var", 1);
		assertProblemCount("''^''", 2);
	}
	//----END exponent Expression----

	//----START signed Expression----
	public void testSignExpression_valid() throws Exception {
		assertNoProblems("+1");
		assertNoProblems("+1.5");
		assertNoProblems("+1e1");
		assertNoProblems("+[]");
		assertNoProblems("+_var");

		assertNoProblems("-1");
		assertNoProblems("-1.5");
		assertNoProblems("-1e1");
		assertNoProblems("-_var");
	}

	public void testSignExpression_bad() throws Exception {
		assertProblemCount("+configFile", 1);
		assertProblemCount("+''", 1);

		assertProblemCount("-[]", 1);
		assertProblemCount("-''", 1);
	}
	//----END signed Expression----

	//----START code block Expression----
	public void testCodeBlockExpression() throws Exception {
		assertNoProblems("{1}");
		assertNoProblems("{+1.5}");
		assertNoProblems("{}");

		assertEquals(getExitTypeForText("{}"), ValueType.CODE);
		assertEquals(getExitTypeForText("{1}"), ValueType.CODE);
		assertEquals(getExitTypeForText("{_var}"), ValueType.CODE);
		assertEquals(getExitTypeForText("{1+1;1}"), ValueType.CODE);
	}
	//----END code block Expression----

	//----START case statement----
	public void testCaseStatement() throws Exception {
		assertNoProblems("case 1;");
		assertNoProblems("case 2:{};");
		assertNoProblems("case '';");
		assertNoProblems("case [];");
		assertNoProblems("case []:{};");
		assertNoProblems("case []:{1};");

		assertNoProblems("case configFile;");

		assertNoProblems("case _var:{};");
		assertNoProblems("case _var;");

		assertEquals(getExitTypeForText("case 1:{};"), ValueType.NOTHING);
		assertEquals(getExitTypeForText("case 1:{2};"), ValueType.NOTHING);
		assertEquals(getExitTypeForText("case 1;"), ValueType.NOTHING);
		assertEquals(getExitTypeForText("case configFile;"), ValueType.NOTHING);
	}
	//----END case statement----

	//----START assignment statement----
	public void testAssignmentStatement() throws Exception {
		assertNoProblems("a = {};");
		assertNoProblems("a={2};");
		assertNoProblems("a = 1+1;");

		assertNoProblems("_var = configFile;");
		assertNoProblems("_var = _var;");

		//this problem should be a grammar error, not a type error
		assertNoProblems("a = ;");

		assertEquals(getExitTypeForText("a = {};"), ValueType.NOTHING);
		assertEquals(getExitTypeForText("a={2};"), ValueType.NOTHING);
		assertEquals(getExitTypeForText("a = 1+1;"), ValueType.NOTHING);
		assertEquals(getExitTypeForText("a = _var;"), ValueType.NOTHING);
	}
	//----END assignment statement----

	//----START quest statement----
	public void testQuestStatement_valid() throws Exception {
		assertNoProblems("? true : false;");
		assertNoProblems("? true : {};");
		assertNoProblems("? _var : 1+1;");

		//this problem should be a grammar error, not a type error
		assertNoProblems("? ;");

		assertEquals(getExitTypeForText("? true : false;"), ValueType.NOTHING);
		assertEquals(getExitTypeForText("? ;"), ValueType.NOTHING);
	}

	public void testQuestStatement_bad() throws Exception {
		assertProblemCount("? 1 : false;", 1);
		assertProblemCount("? {} : false;", 1);
	}
	//----END quest statement----
}
