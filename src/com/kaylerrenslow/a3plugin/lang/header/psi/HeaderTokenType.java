package com.kaylerrenslow.a3plugin.lang.header.psi;

import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;

import java.util.Hashtable;

/**
 * Created by Kayler on 10/31/2015.
 */
public class HeaderTokenType extends IElementType{
	private static final Hashtable<String, String> debugNameLookup = new Hashtable<>();

	private String newDebugName;

	static {
		debugNameLookup.put("NUMBER_LITERAL", "number");
		debugNameLookup.put("HEX_LITERAL", "hex");
		debugNameLookup.put("STRING_LITERAL", "String");
		debugNameLookup.put("INCLUDE_VALUE_ANGBR", "INCLUDE_VALUE_ANGBR");

		debugNameLookup.put("CLASS", "class");

		debugNameLookup.put("PREPROCESS_INCLUDE", "#include");
		/*
		debugNameLookup.put("PREPROCESS_DEFINE", "#define");
		debugNameLookup.put("PREPROCESS_UNDEF", "#undef");
		debugNameLookup.put("PREPROCESS_IF_DEF", "#ifdef");
		debugNameLookup.put("PREPROCESS_IF_N_DEF", "#ifndef");
		debugNameLookup.put("PREPROCESS_ELSE", "#else");
		debugNameLookup.put("PREPROCESS_END_IF", "#endif");
		debugNameLookup.put("PREPROCESS_HASH", "#hash");
		debugNameLookup.put("PREPROCESS_HASH_HASH", "##");
		debugNameLookup.put("PREPROCESS_EXEC", "__EXEC");
		debugNameLookup.put("PREPROCESS_EVAL", "__EVAL");
		debugNameLookup.put("PREPROCESS_LINE", "__LINE__");
		debugNameLookup.put("PREPROCESS_FILE", "__FILE__");
		*/
		debugNameLookup.put("IDENTIFIER", "Identifier");

		//debugNameLookup.put("BSLASH", "\\");

		debugNameLookup.put("EQ", "=");

		debugNameLookup.put("PLUS", "+");
		debugNameLookup.put("MINUS", "-");
		debugNameLookup.put("FSLASH", "/");
		debugNameLookup.put("ASTERISK", "*");

		debugNameLookup.put("LBRACE", "{");
		debugNameLookup.put("RBRACE", "}");

		debugNameLookup.put("LPAREN", "(");
		debugNameLookup.put("RPAREN", ");");

		debugNameLookup.put("BRACKET_PAIR", "[]");
		debugNameLookup.put("COMMA", ",");
		debugNameLookup.put("COLON", ":");
		debugNameLookup.put("SEMICOLON", ";");
		debugNameLookup.put("BAD_CHARACTER", "bad character");
	}

	private static String getNewDebugName(String debugName) {
		String s = debugNameLookup.get(debugName);
		return s != null ? s : debugName;
	}

	public HeaderTokenType(String debugName) {
		super(debugName, HeaderLanguage.INSTANCE);
		this.newDebugName = getNewDebugName(debugName);
	}

	@Override
	public String toString() {
		return newDebugName;
	}
}
