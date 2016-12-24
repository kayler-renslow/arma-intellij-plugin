package com.kaylerrenslow.a3plugin.lang.sqf.psi;

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
import com.kaylerrenslow.a3plugin.lang.sqf.SQFLanguage;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFLexerAdapter;
import com.kaylerrenslow.a3plugin.lang.sqf.parser.SQFParser;
import org.jetbrains.annotations.NotNull;

/**
 * Creates JFlex lexer and Psi parser for SQF language
 *
 * @author Kayler
 * @since 10/31/2015
 */
public class SQFParserDefinition implements ParserDefinition {
	private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
	private static final TokenSet COMMENTS = TokenSet.create(SQFTypes.INLINE_COMMENT, SQFTypes.BLOCK_COMMENT);
	private static final TokenSet STRING_LITERALS = TokenSet.create(SQFTypes.STRING_LITERAL);
	private static final IFileElementType FILE = new IFileElementType(Language.<SQFLanguage>findInstance(SQFLanguage.class));

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
