package com.kaylerrenslow.a3plugin.lang.sqf.codeStyle.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFLexerAdapter;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFParserDefinition;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * SyntaxHighlighter extension point for SQF language
 *
 * @author Kayler
 * @since 11/01/2015
 */
public class SQFSyntaxHighlighter extends SyntaxHighlighterBase {
	public static final TextAttributesKey COMMENT = createTextAttributesKey("A3_SQF_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
	public static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("A3_SQF_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
	public static final TextAttributesKey GLOBAL_VAR = createTextAttributesKey("A3_SQF_GLOBAL_VAR", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
	public static final TextAttributesKey LOCAL_VAR = createTextAttributesKey("A3_SQF_LOCAL_VAR", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);

	public static final TextAttributesKey MAGIC_VAR = createTextAttributesKey("A3_SQF_MAGIC_VARIABLE");
	public static final TextAttributesKey COMMAND = createTextAttributesKey("A3_SQF_COMMAND", DefaultLanguageHighlighterColors.METADATA);
	public static final TextAttributesKey OPERATOR = createTextAttributesKey("A3_SQF_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);

	public static final TextAttributesKey STRING = createTextAttributesKey("A3_SQF_STRING", DefaultLanguageHighlighterColors.STRING);
	public static final TextAttributesKey NUM = createTextAttributesKey("A3_SQF_NUM", DefaultLanguageHighlighterColors.NUMBER);

	public static final TextAttributesKey BRACKET = createTextAttributesKey("A3_SQF_BRACKET", DefaultLanguageHighlighterColors.BRACKETS);
	public static final TextAttributesKey BRACE = createTextAttributesKey("A3_SQF_BRACE", DefaultLanguageHighlighterColors.BRACES);
	public static final TextAttributesKey PAREN = createTextAttributesKey("A3_SQF_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
	public static final TextAttributesKey COMMA = createTextAttributesKey("A3_SQF_COMMA", DefaultLanguageHighlighterColors.COMMA);

	public static final TextAttributesKey COMMENT_NOTE = createTextAttributesKey("A3_SQF_COMMENT_NOTE");

	private static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{OPERATOR};
	private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
	private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};

	private static final TextAttributesKey[] MAGIC_VAR_KEYS = new TextAttributesKey[]{MAGIC_VAR};

	private static final TextAttributesKey[] COMMAND_KEYS = new TextAttributesKey[]{COMMAND};
	private static final TextAttributesKey[] GLOBAL_VAR_KEYS = new TextAttributesKey[]{GLOBAL_VAR};
	private static final TextAttributesKey[] LOCAL_VAR_KEYS = new TextAttributesKey[]{LOCAL_VAR};

	private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
	private static final TextAttributesKey[] NUM_KEYS = new TextAttributesKey[]{NUM};

	private static final TextAttributesKey[] BRACKET_KEYS = new TextAttributesKey[]{BRACKET};
	private static final TextAttributesKey[] BRACE_KEYS = new TextAttributesKey[]{BRACE};
	private static final TextAttributesKey[] PAREN_KEYS = new TextAttributesKey[]{PAREN};
	private static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{COMMA};

	private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

	@NotNull
	@Override
	public Lexer getHighlightingLexer() {
		return new SQFLexerAdapter();
	}


	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
		if (tokenType.equals(SQFTypes.LBRACE) || tokenType.equals(SQFTypes.RBRACE)) {
			return BRACE_KEYS;
		}
		if (tokenType.equals(SQFTypes.LBRACKET) || tokenType.equals(SQFTypes.RBRACKET)) {
			return BRACKET_KEYS;
		}
		if (tokenType.equals(SQFTypes.LPAREN) || tokenType.equals(SQFTypes.RPAREN)) {
			return PAREN_KEYS;
		}
		if (tokenType.equals(SQFTypes.COMMA)) {
			return COMMA_KEYS;
		}
		if (SQFParserDefinition.COMMENTS.contains(tokenType)) {
			return COMMENT_KEYS;
		}
		if (tokenType.equals(SQFTypes.STRING_LITERAL)) {
			return STRING_KEYS;
		}
		if (tokenType.equals(SQFTypes.INTEGER_LITERAL) || tokenType.equals(SQFTypes.DEC_LITERAL) || tokenType.equals(SQFTypes.HEX_LITERAL)) {
			return NUM_KEYS;
		}
		if (tokenType.equals(SQFTypes.LOCAL_VAR)) {
			return LOCAL_VAR_KEYS;
		}
		if (tokenType.equals(SQFTypes.GLOBAL_VAR)) {
			return GLOBAL_VAR_KEYS;
		}
		if (tokenType.equals(SQFTypes.LANG_VAR)) {
			return MAGIC_VAR_KEYS;
		}
		if (SQFStatic.isCommand(tokenType)) {
			return COMMAND_KEYS;
		}
		for (IElementType e : SQFParserDefinition.OPERATORS) {
			if (tokenType.equals(e)) {
				return OPERATOR_KEYS;
			}
		}
		if (tokenType.equals(TokenType.BAD_CHARACTER)) {
			return BAD_CHAR_KEYS;
		}
		return EMPTY_KEYS;
	}

}
