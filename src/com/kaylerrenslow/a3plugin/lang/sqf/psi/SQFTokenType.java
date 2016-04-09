package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFLanguage;

import java.util.Hashtable;

/**
 * @author Kayler
 * TokenType extension point for SQF language
 * Created on 10/31/2015.
 */
public class SQFTokenType extends IElementType{

	private static final Hashtable<String, String> debugNameLookup = new Hashtable<>();
	private final String tokenName;

	private String newDebugName;

	static {
		debugNameLookup.put("INTEGER_LITERAL", "Number");
		debugNameLookup.put("DEC_LITERAL", "Number");
		debugNameLookup.put("STRING_LITERAL", "String");
		debugNameLookup.put("NIL", "nil");
		debugNameLookup.put("TYPE_NULL", "null value");
		debugNameLookup.put("PRIVATE", "private");
		debugNameLookup.put("SCOPE_NAME", "scopeName");
		debugNameLookup.put("BREAK", "break");
		debugNameLookup.put("BREAK_TO", "breakTo");
		debugNameLookup.put("BREAK_OUT", "breakOut");
		debugNameLookup.put("CONTINUE", "continue");
		debugNameLookup.put("FOR", "for");
		debugNameLookup.put("TO", "to");
		debugNameLookup.put("FOR_EACH", "forEach");
		debugNameLookup.put("FROM", "from");
		debugNameLookup.put("WHILE", "while");
		debugNameLookup.put("GOTO", "goto");
		debugNameLookup.put("ASSERT", "assert");
		debugNameLookup.put("STEP", "wizardStep");
		debugNameLookup.put("IF", "if");
		debugNameLookup.put("THEN", "then");
		debugNameLookup.put("ELSE", "else");
		debugNameLookup.put("SWITCH", "switch");
		debugNameLookup.put("CASE", "case");
		debugNameLookup.put("DEFAULT", "default");
		debugNameLookup.put("DO", "do");
		debugNameLookup.put("WAIT_UNTIL", "waitUntil");
		debugNameLookup.put("EXIT_WITH", "exitWith");
		debugNameLookup.put("LANG_VAR", "Identifier");
		debugNameLookup.put("WITH", "with");
		debugNameLookup.put("NAMESPACE", "Namespace");
		debugNameLookup.put("MOD", "mod");
		debugNameLookup.put("NOT", "not");
		debugNameLookup.put("AND", "and");
		debugNameLookup.put("OR", "or");
		debugNameLookup.put("TRY", "try");
		debugNameLookup.put("CATCH", "catch");
		debugNameLookup.put("THROW", "throw");
		debugNameLookup.put("CONFIG", "Config File");
		debugNameLookup.put("COMMAND", "Command");
		debugNameLookup.put("LOCAL_VAR", "Identifier");
		debugNameLookup.put("GLOBAL_VAR", "Identifier");
		debugNameLookup.put("DQOUTE", "\"");
		debugNameLookup.put("EQEQ", "==");
		debugNameLookup.put("NE", "!=");
		debugNameLookup.put("GTGT", ">>");
		debugNameLookup.put("LE", "<=");
		debugNameLookup.put("GE", ">=");
		debugNameLookup.put("AMPAMP", "&&");
		debugNameLookup.put("BARBAR", "||");
		debugNameLookup.put("ASTERISK", "*");
		debugNameLookup.put("EQ", "=");
		debugNameLookup.put("PERC", "%");
		debugNameLookup.put("PLUS", "+");
		debugNameLookup.put("MINUS", "-");
		debugNameLookup.put("FSLASH", "/");
		debugNameLookup.put("CARET", "^");
		debugNameLookup.put("LT", "<");
		debugNameLookup.put("GT", ">");
		debugNameLookup.put("EXCL", "!");
		debugNameLookup.put("LPAREN", "(");
		debugNameLookup.put("RPAREN", ")");
		debugNameLookup.put("LBRACE", "{");
		debugNameLookup.put("RBRACE", "}");
		debugNameLookup.put("LBRACKET", "[");
		debugNameLookup.put("RBRACKET", "]");
		debugNameLookup.put("COMMA", ",");
		debugNameLookup.put("SEMICOLON", ";");
		debugNameLookup.put("QUEST", "?");
		debugNameLookup.put("COLON", ":");
		debugNameLookup.put("BAD_CHARACTER", "bad character");
	}

	private static String getNewDebugName(String debugName){
		String s = debugNameLookup.get(debugName);
		return s != null ? s : debugName;
	}

	public SQFTokenType(String debugName) {
		super(debugName, SQFLanguage.INSTANCE);
		this.newDebugName = getNewDebugName(debugName);
		this.tokenName = debugName;
	}

	@Override
	public String toString(){
		return this.newDebugName;
	}

	public String getTokenName(){
		return this.tokenName;
	}
}
