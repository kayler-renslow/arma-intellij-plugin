package com.kaylerrenslow.a3plugin.lang.header.codeStyle.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * Created on 03/18/2016.
 */
public class HeaderFormattingBuilder implements FormattingModelBuilder{
	@NotNull
	@Override
	public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
		return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(), new HeaderBlock(element.getNode(), Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(), createSpaceBuilder(settings)), settings);
	}

	private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
		//@formatter:off
		return new SpacingBuilder(settings, HeaderLanguage.INSTANCE)
				.around(TokenSet.create(HeaderTypes.PLUS, HeaderTypes.MINUS)).spaceIf(settings.SPACE_AROUND_ADDITIVE_OPERATORS)
				.around(TokenSet.create(HeaderTypes.EQ)).spaceIf(settings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
				.around(TokenSet.create(HeaderTypes.FSLASH, HeaderTypes.ASTERISK)).spaceIf(settings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS)
				.after(HeaderTypes.SEMICOLON).spaceIf(settings.SPACE_AFTER_SEMICOLON)
				.before(HeaderTypes.COMMA).spaceIf(settings.SPACE_BEFORE_COMMA)
				.after(HeaderTypes.COMMA).spaceIf(settings.SPACE_AFTER_COMMA)
				.before(HeaderTypes.COLON).spaceIf(settings.SPACE_BEFORE_COLON)
				.after(HeaderTypes.COLON).spaceIf(settings.SPACE_AFTER_COLON);
		//@formatter:on
	}

	@Nullable
	@Override
	public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
		System.out.println(elementAtOffset.getElementType());
		return null;
	}
}
