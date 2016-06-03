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
		debugNameLookup.put("COMMAND_TOKEN", "Command");
		debugNameLookup.put("LOCAL_VAR", "Identifier");
		debugNameLookup.put("GLOBAL_VAR", "Identifier");
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
