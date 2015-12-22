package com.kaylerrenslow.plugin;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.plugin.psi.SQFTypes;
import com.thaiopensource.xml.dtd.om.Def;
import org.jetbrains.annotations.NotNull;

import javax.xml.soap.Text;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Created by Kayler on 11/01/2015.
 */
public class SQFSyntaxHighlighter extends SyntaxHighlighterBase{
	public static final TextAttributesKey COMMENT = createTextAttributesKey("SQF_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
	public static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SQF_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
	public static final TextAttributesKey IDENTIFIER = createTextAttributesKey("IDENTIFIER", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
	public static final TextAttributesKey LOCAL_VAR = createTextAttributesKey("LOCAL_VAR", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);

	public static final TextAttributesKey CONSTANT = createTextAttributesKey("CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);
	public static final TextAttributesKey KEYWORD = createTextAttributesKey("KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey COMMAND = createTextAttributesKey("COMMAND", DefaultLanguageHighlighterColors.METADATA);
	public static final TextAttributesKey OPERATOR = createTextAttributesKey("OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);

	public static final TextAttributesKey STRING = createTextAttributesKey("STRING", DefaultLanguageHighlighterColors.STRING);
	public static final TextAttributesKey NUM = createTextAttributesKey("NUM", DefaultLanguageHighlighterColors.NUMBER);

	private static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{OPERATOR};
	private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
	private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};

	private static final TextAttributesKey[] CONSTANT_KEYS = new TextAttributesKey[]{CONSTANT};

	private static final TextAttributesKey[] COMMAND_KEYS = new TextAttributesKey[]{COMMAND};
	private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
	private static final TextAttributesKey[] LOCAL_VAR_KEYS = new TextAttributesKey[]{LOCAL_VAR};
	private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};

	private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
	private static final TextAttributesKey[] NUM_KEYS = new TextAttributesKey[]{NUM};

	private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

	private static final IElementType[] OPERATORS = {SQFTypes.EQEQ, SQFTypes.EQ, SQFTypes.ASTERISK, SQFTypes.NE, SQFTypes.PERC, SQFTypes.MOD, SQFTypes.PLUS, SQFTypes.MINUS, SQFTypes.FSLASH, SQFTypes.CARET, SQFTypes.GTGT, SQFTypes.GT, SQFTypes.GE, SQFTypes.LT, SQFTypes.LE, SQFTypes.EXCL, SQFTypes.NOT, SQFTypes.AMPAMP, SQFTypes.AND, SQFTypes.BARBAR, SQFTypes.OR, SQFTypes.QUEST, SQFTypes.COLON};
	private static final IElementType[] KEYWORDS = {SQFTypes.WITH, SQFTypes.TRUE, SQFTypes.FALSE, SQFTypes.NIL, SQFTypes.TYPE_NULL, SQFTypes.PRIVATE, SQFTypes.SCOPE_NAME, SQFTypes.BREAK, SQFTypes.BREAK_TO, SQFTypes.BREAK_OUT, SQFTypes.CONTINUE, SQFTypes.FOR, SQFTypes.TO, SQFTypes.FOR_EACH, SQFTypes.FROM, SQFTypes.WHILE, SQFTypes.GOTO, SQFTypes.ASSERT, SQFTypes.IF, SQFTypes.THEN, SQFTypes.ELSE, SQFTypes.SWITCH, SQFTypes.CASE, SQFTypes.DEFAULT, SQFTypes.DO};
	private static final IElementType[] CONSTANTS = {SQFTypes.LANG_CONSTANT, SQFTypes.NAMESPACE, SQFTypes.CONFIG};
	private static final IElementType[] COMMANDS = {SQFTypes.COMMAND, SQFTypes.DISABLE_SERIALIZATION, SQFTypes.APPEND, SQFTypes.SELECT, SQFTypes.SET, SQFTypes.COUNT, SQFTypes.FIND, SQFTypes.FORMAT, SQFTypes.COMPILE, SQFTypes.COMPILE_FINAL, SQFTypes.CALL, SQFTypes.CALL_EXTENSION, SQFTypes.EXEC_EDITOR_SCRIPT, SQFTypes.EXEC, SQFTypes.EXEC_FSM, SQFTypes.EXEC_VM, SQFTypes.SPAWN, SQFTypes.LOAD_FILE, SQFTypes.PREPROCESS_FILE};


	@NotNull
	@Override
	public Lexer getHighlightingLexer() {
		return new SQFLexerAdapter();
	}

	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
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
			return IDENTIFIER_KEYS;
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
