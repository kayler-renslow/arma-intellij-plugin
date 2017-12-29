package com.kaylerrenslow.armaplugin.lang.sqf.psi.codestyle;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFLexerAdapter;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFParserDefinition;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * SyntaxHighlighter extension point for SQF language
 *
 * @author Kayler
 * @since 11/01/2015
 */
public class SQFSyntaxHighlighter extends SyntaxHighlighterBase {
	public static final TextAttributesKey COMMENT = createTextAttributesKey("ARMA_SQF_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
	public static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("ARMA_SQF_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
	public static final TextAttributesKey GLOBAL_VAR = createTextAttributesKey("ARMA_SQF_GLOBAL_VAR", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
	public static final TextAttributesKey LOCAL_VAR = createTextAttributesKey("ARMA_SQF_LOCAL_VAR", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);

	/**
	 * Key for variables like _x and _this
	 */
	public static final TextAttributesKey MAGIC_VAR = createTextAttributesKey("ARMA_SQF_MAGIC_VARIABLE");
	public static final TextAttributesKey COMMAND = createTextAttributesKey("ARMA_SQF_COMMAND");
	public static final TextAttributesKey OPERATOR = createTextAttributesKey("ARMA_SQF_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
	public static final TextAttributesKey CONTROL_STRUCTURE_COMMAND = createTextAttributesKey("ARMA_SQF_CONTROL_STRUCTURE_COMMAND", DefaultLanguageHighlighterColors.KEYWORD);

	public static final TextAttributesKey STRING = createTextAttributesKey("ARMA_SQF_STRING", DefaultLanguageHighlighterColors.STRING);
	public static final TextAttributesKey NUM = createTextAttributesKey("ARMA_SQF_NUM", DefaultLanguageHighlighterColors.NUMBER);

	public static final TextAttributesKey BRACKET = createTextAttributesKey("ARMA_SQF_BRACKET", DefaultLanguageHighlighterColors.BRACKETS);
	public static final TextAttributesKey BRACE = createTextAttributesKey("ARMA_SQF_BRACE", DefaultLanguageHighlighterColors.BRACES);
	public static final TextAttributesKey PAREN = createTextAttributesKey("ARMA_SQF_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
	public static final TextAttributesKey COMMA = createTextAttributesKey("ARMA_SQF_COMMA", DefaultLanguageHighlighterColors.COMMA);

	private static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{OPERATOR};
	private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
	private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};

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
		//assign a IElementType a TextAttributesKey array.
		//more syntax highlighting can be done via Annotators

		if (tokenType == null) {
			return EMPTY_KEYS;
		}

		if (tokenType.equals(SQFTypes.L_CURLY_BRACE) || tokenType.equals(SQFTypes.R_CURLY_BRACE)) {
			return BRACE_KEYS;
		}
		if (tokenType.equals(SQFTypes.L_SQ_BRACKET) || tokenType.equals(SQFTypes.R_SQ_BRACKET)) {
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
		if (SQFParserDefinition.isCommand(tokenType)) {
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