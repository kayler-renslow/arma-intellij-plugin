package com.kaylerrenslow.a3plugin.lang.header.codeStyle.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * FormattingModelBuilder implementation for Header language
 * Created on 03/18/2016.
 */
public class HeaderFormattingBuilder implements FormattingModelBuilder{
	@NotNull
	@Override
	public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
		return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(), new HeaderBlock(element.getNode(), Wrap.createWrap(WrapType.NONE, false), null, Indent.getNoneIndent(), settings, createSpaceBuilder(settings)), settings);
	}

	public static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
		CommonCodeStyleSettings commonSettings = settings.getCommonSettings(HeaderLanguage.INSTANCE);
		//@formatter:off
		return new SpacingBuilder(settings, HeaderLanguage.INSTANCE)
				.around(TokenSet.create(HeaderTypes.PLUS, HeaderTypes.MINUS)).spaceIf(commonSettings.SPACE_AROUND_ADDITIVE_OPERATORS)
				.around(TokenSet.create(HeaderTypes.EQ)).spaceIf(commonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
				.around(TokenSet.create(HeaderTypes.FSLASH, HeaderTypes.ASTERISK)).spaceIf(commonSettings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS)
				.after(HeaderTypes.SEMICOLON).spaceIf(commonSettings.SPACE_AFTER_SEMICOLON)
				.before(HeaderTypes.COMMA).spaceIf(commonSettings.SPACE_BEFORE_COMMA)
				.after(HeaderTypes.COMMA).spaceIf(commonSettings.SPACE_AFTER_COMMA)
				.before(HeaderTypes.COLON).spaceIf(commonSettings.SPACE_BEFORE_COLON)
				.after(HeaderTypes.COLON).spaceIf(commonSettings.SPACE_AFTER_COLON)
				.withinPairInside(HeaderTypes.LBRACE, HeaderTypes.RBRACE, HeaderTypes.ARRAY).spaceIf(commonSettings.SPACE_WITHIN_ARRAY_INITIALIZER_BRACES)
				.betweenInside(HeaderTypes.CLASS_STUB, HeaderTypes.CLASS_CONTENT, HeaderTypes.CLASS_DECLARATION).spaceIf(commonSettings.SPACE_BEFORE_CLASS_LBRACE)
				.beforeInside(HeaderTypes.LBRACE, HeaderTypes.CLASS_CONTENT).spaceIf(commonSettings.SPACE_BEFORE_CLASS_LBRACE)
				.before(HeaderTypes.INLINE_COMMENT).spaceIf(true)
				.before(HeaderTypes.BLOCK_COMMENT).spaceIf(true)
				;
		//@formatter:on
	}

	@Nullable
	@Override
	public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
		return null;
	}
}
