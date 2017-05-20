package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFLanguage;
import com.kaylerrenslow.armaplugin.lang.sqf.parser.SQFParser;
import org.jetbrains.annotations.NotNull;

/**
 * Creates JFlex lexer and Psi parser for SQF language.
 *
 * Custom IElementTypes/SQFTokenTypes for SQF language are also created here (they are usually for the Lexer).
 *
 * @author Kayler
 * @since 10/31/2015
 */
public class SQFParserDefinition implements ParserDefinition {
	private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);

	public static final IElementType INLINE_COMMENT = new SQFTokenType("INLINE_COMMENT");
	public static final IElementType BLOCK_COMMENT = new SQFTokenType("BLOCK_COMMENT");
	public static final TokenSet COMMENTS = TokenSet.create(
			INLINE_COMMENT,
			BLOCK_COMMENT
	);

	public static final TokenSet NUMBER_LITERALS = TokenSet.create(SQFTypes.DEC_LITERAL, SQFTypes.INTEGER_LITERAL);
	public static final TokenSet IDENTIFIERS = TokenSet.create(SQFTypes.GLOBAL_VAR, SQFTypes.LOCAL_VAR, SQFTypes.VARIABLE);
	public static final TokenSet STRING_LITERALS = TokenSet.create(SQFTypes.STRING_LITERAL);

	public static final IElementType[] OPERATORS = {
			SQFTypes.EQEQ, SQFTypes.EQ, SQFTypes.ASTERISK, SQFTypes.NE, SQFTypes.PERC, SQFTypes.PLUS, SQFTypes.MINUS,
			SQFTypes.FSLASH, SQFTypes.CARET, SQFTypes.GTGT, SQFTypes.GT, SQFTypes.GE, SQFTypes.LT, SQFTypes.LE,
			SQFTypes.EXCL, SQFTypes.AMPAMP, SQFTypes.BARBAR, SQFTypes.QUEST, SQFTypes.COLON
	};


	private static final IFileElementType FILE = new IFileElementType(Language.<SQFLanguage>findInstance(SQFLanguage.class));

	/**
	 * @return true if the given type refers to a command, false otherwise
	 */
	public static boolean isCommand(@NotNull IElementType type) {
		return type == SQFTypes.COMMAND || type == SQFTypes.COMMAND_TOKEN;
	}

	@NotNull
	@Override
	public Lexer createLexer(Project project) {
		return new SQFLexerAdapter();
	}

	@Override
	public PsiParser createParser(Project project) {
		return new SQFParser();
	}

	@Override
	public IFileElementType getFileNodeType() {
		return FILE;
	}

	@NotNull
	@Override
	public TokenSet getWhitespaceTokens() {
		return WHITE_SPACES;
	}

	@NotNull
	@Override
	public TokenSet getCommentTokens() {
		return COMMENTS;
	}

	@NotNull
	@Override
	public TokenSet getStringLiteralElements() {
		return STRING_LITERALS;
	}

	@NotNull
	@Override
	public PsiElement createElement(ASTNode node) {
		return SQFTypes.Factory.createElement(node);
	}

	@Override
	public PsiFile createFile(FileViewProvider viewProvider) {
		return new SQFFile(viewProvider);
	}

	@Override
	public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
		return SpaceRequirements.MAY;
	}
}
