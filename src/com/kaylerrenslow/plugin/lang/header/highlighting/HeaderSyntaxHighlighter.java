package com.kaylerrenslow.plugin.lang.header.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.plugin.lang.header.HeaderLexerAdapter;

import com.kaylerrenslow.plugin.lang.header.psi.HeaderTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Created by Kayler on 11/01/2015.
 */
public class HeaderSyntaxHighlighter extends SyntaxHighlighterBase{
	public static final TextAttributesKey COMMENT = createTextAttributesKey("SQF_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
	public static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("HEADER_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

	public static final TextAttributesKey CONSTANT = createTextAttributesKey("CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);
	public static final TextAttributesKey IDENTIFIER = createTextAttributesKey("IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);

	public static final TextAttributesKey KEYWORD = createTextAttributesKey("KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey OPERATOR = createTextAttributesKey("OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);

	public static final TextAttributesKey BRACKET = createTextAttributesKey("BRACKET", DefaultLanguageHighlighterColors.BRACKETS);
	public static final TextAttributesKey BRACE = createTextAttributesKey("BRACE", DefaultLanguageHighlighterColors.BRACES);
	public static final TextAttributesKey PAREN = createTextAttributesKey("PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
	public static final TextAttributesKey COMMA = createTextAttributesKey("COMMA", DefaultLanguageHighlighterColors.COMMA);

	public static final TextAttributesKey PREPROCESSOR = createTextAttributesKey("PREPROCESSOR", DefaultLanguageHighlighterColors.INSTANCE_FIELD);

	public static final TextAttributesKey STRING = createTextAttributesKey("STRING", DefaultLanguageHighlighterColors.STRING);
	public static final TextAttributesKey NUM = createTextAttributesKey("NUM", DefaultLanguageHighlighterColors.NUMBER);
	
	
	private static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{OPERATOR};
	private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
	private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};

	private static final TextAttributesKey[] CONSTANT_KEYS = new TextAttributesKey[]{CONSTANT};

	private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
	private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};

	private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
	private static final TextAttributesKey[] NUM_KEYS = new TextAttributesKey[]{NUM};

	private static final TextAttributesKey[] BRACKET_KEYS = new TextAttributesKey[]{BRACKET};
	private static final TextAttributesKey[] BRACE_KEYS = new TextAttributesKey[]{BRACE};
	//private static final TextAttributesKey[] PAREN_KEYS = new TextAttributesKey[]{PAREN};
	private static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{COMMA};

	private static final TextAttributesKey[] PREPROCESSOR_KEYS = new TextAttributesKey[]{PREPROCESSOR};

	private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

	private static final IElementType[] OPERATORS = {HeaderTypes.EQ, HeaderTypes.BRACKET_PAIR, HeaderTypes.COLON, HeaderTypes.FSLASH, HeaderTypes.MINUS, HeaderTypes.PLUS};
	private static final IElementType[] KEYWORDS = {HeaderTypes.CLASS};
	private static final IElementType[] CONSTANTS = {};
	private static final IElementType[] PREPROCESSORS = {HeaderTypes.PREPROCESS_INCLUDE};


	@NotNull
	@Override
	public Lexer getHighlightingLexer() {
		return new HeaderLexerAdapter();
	}


	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
		if (tokenType.equals(HeaderTypes.LBRACE) || tokenType.equals(HeaderTypes.RBRACE)){
			return BRACE_KEYS;
		}
		if(tokenType.equals(HeaderTypes.BRACKET_PAIR)){
			return BRACKET_KEYS;
		}
		/*
		if(tokenType.equals(HeaderTypes.LPAREN) || tokenType.equals(HeaderTypes.RPAREN)){
			return PAREN_KEYS;
		}
		*/
		if(tokenType.equals(HeaderTypes.COMMA)){
			return COMMA_KEYS;
		}
		if (tokenType.equals(HeaderTypes.COMMENT)){
			return COMMENT_KEYS;
		}
		if(tokenType.equals(HeaderTypes.STRING_LITERAL)){
			return STRING_KEYS;
		}
		if(tokenType.equals(HeaderTypes.NUMBER_LITERAL)){
			return NUM_KEYS;
		}
		if(tokenType.equals(HeaderTypes.IDENTIFIER)){
			return IDENTIFIER_KEYS;
		}
		for(IElementType e: KEYWORDS){
			if(tokenType.equals(e)){
				return KEYWORD_KEYS;
			}
		}
		for(IElementType e: OPERATORS){
			if(tokenType.equals(e)){
				return OPERATOR_KEYS;
			}
		}
		for(IElementType e: CONSTANTS){
			if(tokenType.equals(e)){
				return CONSTANT_KEYS;
			}
		}
		for(IElementType e: PREPROCESSORS){
			if(tokenType.equals(e)){
				return PREPROCESSOR_KEYS;
			}
		}
		if (tokenType.equals(TokenType.BAD_CHARACTER)){
			return BAD_CHAR_KEYS;
		}
		return EMPTY_KEYS;
	}
}
