package com.kaylerrenslow.a3plugin.lang.header.psi;

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
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLexerAdapter;
import com.kaylerrenslow.a3plugin.lang.header.parser.HeaderParser;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * ParserDefinition implementation for Header language. This is in charge of creating the Header language's PSI parser and JFlex lexer.
 * Created on 10/31/2015.
 */
public class HeaderParserDefinition implements ParserDefinition{
	private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
	private static final TokenSet COMMENTS = TokenSet.create(HeaderTypes.INLINE_COMMENT, HeaderTypes.BLOCK_COMMENT);
	private static final TokenSet STRINGS = TokenSet.create(HeaderTypes.STRING_LITERAL);
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
		return new HeaderFile(viewProvider);
	}

	@Override
	public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
		return SpaceRequirements.MAY;
	}
}
