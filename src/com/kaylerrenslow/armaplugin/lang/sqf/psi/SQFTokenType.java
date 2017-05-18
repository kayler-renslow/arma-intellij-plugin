package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFLanguage;

import java.util.Hashtable;

/**
 * TokenType extension point for SQF language.
 * A TokenType is similar to IElementType, but IElementType is for the parser and TokenType is for the lexer.
 *
 * @author Kayler
 * @since 10/31/2015
 * @see SQFElementType
 */
public class SQFTokenType extends IElementType {

	private static final Hashtable<String, String> debugNameLookup = new Hashtable<>();
	private final String tokenName;

	private String newDebugName;

	static {
		debugNameLookup.put("INTEGER_LITERAL", "Integer");
		debugNameLookup.put("DEC_LITERAL", "Decimal");
		debugNameLookup.put("STRING_LITERAL", "String");
		debugNameLookup.put("COMMAND_TOKEN", "Command");
		debugNameLookup.put("LOCAL_VAR", "Local Var");
		debugNameLookup.put("GLOBAL_VAR", "Global Var");
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
	}

	private static String getNewDebugName(String debugName) {
		String s = debugNameLookup.get(debugName);
		return s != null ? s : debugName;
	}

	public SQFTokenType(String debugName) {
		super(debugName, SQFLanguage.INSTANCE);
		this.newDebugName = getNewDebugName(debugName);
		this.tokenName = debugName;
	}

	@Override
	public String toString() {
		return this.newDebugName;
	}

	public String getTokenName() {
		return this.tokenName;
	}
}
