package com.kaylerrenslow.armaplugin.lang.header.psi;

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
import com.kaylerrenslow.armaplugin.lang.header.HeaderLanguage;
import com.kaylerrenslow.armaplugin.lang.header.parser.HeaderParser;
import org.jetbrains.annotations.NotNull;

/**
 * ParserDefinition implementation for Header language. This is in charge of creating the Header language's PSI parser and JFlex lexer.
 *
 * @author Kayler
 * @since 10/31/2015
 */
public class HeaderParserDefinition implements ParserDefinition {
	private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);

	public static final IElementType BLOCK_COMMENT = new HeaderTokenType("BLOCK_COMMENT");
	public static final IElementType INLINE_COMMENT = new HeaderTokenType("INLINE_COMMENT");
	public static final TokenSet STRINGS = TokenSet.create(HeaderTypes.STRING_LITERAL);
	public static final TokenSet IDENTIFERS = TokenSet.create(HeaderTypes.IDENTIFIER);
	public static final TokenSet COMMENTS = TokenSet.create(BLOCK_COMMENT, INLINE_COMMENT);
	public static final TokenSet LITERALS = TokenSet.create(HeaderTypes.HEX_LITERAL, HeaderTypes.NUMBER_LITERAL);

	private static final IFileElementType FILE = new IFileElementType(Language.<HeaderLanguage>findInstance(HeaderLanguage.class));

	@NotNull
	@Override
	public Lexer createLexer(Project project) {
		return new HeaderLexerAdapter();
	}

	@Override
	public PsiParser createParser(Project project) {
		return new HeaderParser();
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
		return STRINGS;
	}

	@NotNull
	@Override
	public PsiElement createElement(ASTNode node) {
		return HeaderTypes.Factory.createElement(node);
	}

	@Override
	public PsiFile createFile(FileViewProvider viewProvider) {
		return new HeaderPsiFile(viewProvider);
	}

	@Override
	public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
		return SpaceRequirements.MAY;
	}

}
