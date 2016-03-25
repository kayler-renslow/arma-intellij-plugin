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
import com.kaylerrenslow.a3plugin.lang.header.codeStyle.HeaderCodeStyleSettings;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.lang.manifest.psi.Header;

/**
 * @author Kayler
 * Created on 03/18/2016.
 */
public class HeaderFormattingBuilder implements FormattingModelBuilder{
	@NotNull
	@Override
	public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
		return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(), new HeaderBlock(element.getNode(), null, null, Indent.getNoneIndent(), settings, createSpaceBuilder(settings)), settings);
	}

	public static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
//		HeaderCodeStyleSettings headerSettings = settings.getCustomSettings(HeaderCodeStyleSettings.class);
		CommonCodeStyleSettings commonSettings = settings.getCommonSettings(HeaderLanguage.INSTANCE);
		//@formatter:off
		return new SpacingBuilder(settings, HeaderLanguage.INSTANCE)
				.around(TokenSet.create(HeaderTypes.PLUS, HeaderTypes.MINUS)).spaceIf(commonSettings.SPACE_AROUND_ADDITIVE_OPERATORS)
				.around(TokenSet.create(HeaderTypes.EQ)).spaceIf(settings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
				.around(TokenSet.create(HeaderTypes.FSLASH, HeaderTypes.ASTERISK)).spaceIf(commonSettings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS)
				.after(HeaderTypes.SEMICOLON).spaceIf(commonSettings.SPACE_AFTER_SEMICOLON)
				.before(HeaderTypes.COMMA).spaceIf(commonSettings.SPACE_BEFORE_COMMA)
				.after(HeaderTypes.COMMA).spaceIf(commonSettings.SPACE_AFTER_COMMA)
				.before(HeaderTypes.COLON).spaceIf(commonSettings.SPACE_BEFORE_COLON)
				.after(HeaderTypes.COLON).spaceIf(commonSettings.SPACE_AFTER_COLON);
		//@formatter:on
	}

	@Nullable
	@Override
	public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
		System.out.println("HeaderFormattingBuilder.getRangeAffectingIndent " + elementAtOffset.getElementType());
		return null;
	}
}
