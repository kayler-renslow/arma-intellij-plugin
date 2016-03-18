package com.kaylerrenslow.a3plugin.lang.sqf.codeStyle.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFLexerAdapter;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Created by Kayler on 11/01/2015.
 */
public class SQFSyntaxHighlighter extends SyntaxHighlighterBase{
	public static final TextAttributesKey COMMENT = createTextAttributesKey("A3_SQF_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
	public static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("A3_SQF_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
	public static final TextAttributesKey GLOBAL_VAR = createTextAttributesKey("A3_SQF_GLOBAL_VAR", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
	public static final TextAttributesKey LOCAL_VAR = createTextAttributesKey("A3_SQF_LOCAL_VAR", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);

	public static final TextAttributesKey CONSTANT = createTextAttributesKey("A3_SQF_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);
	public static final TextAttributesKey KEYWORD = createTextAttributesKey("A3_SQF_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey COMMAND = createTextAttributesKey("A3_SQF_COMMAND", DefaultLanguageHighlighterColors.METADATA);
	public static final TextAttributesKey OPERATOR = createTextAttributesKey("A3_SQF_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);

	public static final TextAttributesKey STRING = createTextAttributesKey("A3_SQF_STRING", DefaultLanguageHighlighterColors.STRING);
	public static final TextAttributesKey NUM = createTextAttributesKey("A3_SQF_NUM", DefaultLanguageHighlighterColors.NUMBER);

	public static final TextAttributesKey BRACKET = createTextAttributesKey("A3_SQF_BRACKET", DefaultLanguageHighlighterColors.BRACKETS);
	public static final TextAttributesKey BRACE = createTextAttributesKey("A3_SQF_BRACE", DefaultLanguageHighlighterColors.BRACES);
	public static final TextAttributesKey PAREN = createTextAttributesKey("A3_SQF_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
	public static final TextAttributesKey COMMA = createTextAttributesKey("A3_SQF_COMMA", DefaultLanguageHighlighterColors.COMMA);

	private static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{OPERATOR};
	private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
	private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};

	private static final TextAttributesKey[] CONSTANT_KEYS = new TextAttributesKey[]{CONSTANT};

	private static final TextAttributesKey[] COMMAND_KEYS = new TextAttributesKey[]{COMMAND};
	private static final TextAttributesKey[] GLOBAL_VAR_KEYS = new TextAttributesKey[]{GLOBAL_VAR};
	private static final TextAttributesKey[] LOCAL_VAR_KEYS = new TextAttributesKey[]{LOCAL_VAR};
	private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};

	private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
	private static final TextAttributesKey[] NUM_KEYS = new TextAttributesKey[]{NUM};

	private static final TextAttributesKey[] BRACKET_KEYS = new TextAttributesKey[]{BRACKET};
	private static final TextAttributesKey[] BRACE_KEYS = new TextAttributesKey[]{BRACE};
	private static final TextAttributesKey[] PAREN_KEYS = new TextAttributesKey[]{PAREN};
	private static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{COMMA};

	private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

	private static final IElementType[] OPERATORS = {SQFTypes.EQEQ, SQFTypes.EQ, SQFTypes.ASTERISK, SQFTypes.NE, SQFTypes.PERC, SQFTypes.PLUS, SQFTypes.MINUS, SQFTypes.FSLASH, SQFTypes.CARET, SQFTypes.GTGT, SQFTypes.GT, SQFTypes.GE,
			SQFTypes.LT, SQFTypes.LE, SQFTypes.EXCL, SQFTypes.AMPAMP, SQFTypes.BARBAR, SQFTypes.QUEST, SQFTypes.COLON};

	private static final IElementType[] KEYWORDS = {SQFTypes.WITH, SQFTypes.TRUE, SQFTypes.FALSE, SQFTypes.NOT, SQFTypes.AND, SQFTypes.OR, SQFTypes.MOD, SQFTypes.NIL, SQFTypes.TYPE_NULL, SQFTypes.PRIVATE, SQFTypes.SCOPE_NAME,
			SQFTypes.BREAK, SQFTypes.BREAK_TO, SQFTypes.BREAK_OUT, SQFTypes.CONTINUE, SQFTypes.FOR, SQFTypes.TO, SQFTypes.STEP, SQFTypes.FOR_EACH, SQFTypes.FROM, SQFTypes.WHILE, SQFTypes.GOTO, SQFTypes.ASSERT, SQFTypes.IF,
			SQFTypes.THEN, SQFTypes.ELSE, SQFTypes.SWITCH, SQFTypes.CASE, SQFTypes.DEFAULT, SQFTypes.DO, SQFTypes.WAIT_UNTIL, SQFTypes.EXIT_WITH, };

	private static final IElementType[] CONSTANTS = {SQFTypes.LANG_CONSTANT, SQFTypes.NAMESPACE, SQFTypes.CONFIG};

	private static final IElementType[] COMMANDS = {SQFTypes.COMMAND};


	@NotNull
	@Override
	public Lexer getHighlightingLexer() {
		return new SQFLexerAdapter();
	}


	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
		if (tokenType.equals(SQFTypes.LBRACE) || tokenType.equals(SQFTypes.RBRACE)){
			return BRACE_KEYS;
		}
		if(tokenType.equals(SQFTypes.LBRACKET) || tokenType.equals(SQFTypes.RBRACKET)){
			return BRACKET_KEYS;
		}
		if(tokenType.equals(SQFTypes.LPAREN) || tokenType.equals(SQFTypes.RPAREN)){
			return PAREN_KEYS;
		}
		if(tokenType.equals(SQFTypes.COMMA)){
			return COMMA_KEYS;
		}
		if (tokenType.equals(SQFTypes.COMMENT)){
			return COMMENT_KEYS;
		}
		if(tokenType.equals(SQFTypes.STRING_LITERAL)){
			return STRING_KEYS;
		}
		if(tokenType.equals(SQFTypes.INTEGER_LITERAL) || tokenType.equals(SQFTypes.DEC_LITERAL)){
			return NUM_KEYS;
		}
		if(tokenType.equals(SQFTypes.LOCAL_VAR)){
			return LOCAL_VAR_KEYS;
		}
		if(tokenType.equals(SQFTypes.IDENTIFIER)){
			return GLOBAL_VAR_KEYS;
		}
		for(IElementType e: COMMANDS){
			if(tokenType.equals(e)){
				return COMMAND_KEYS;
			}
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
		if (tokenType.equals(TokenType.BAD_CHARACTER)){
			return BAD_CHAR_KEYS;
		}
		return EMPTY_KEYS;
	}

}
