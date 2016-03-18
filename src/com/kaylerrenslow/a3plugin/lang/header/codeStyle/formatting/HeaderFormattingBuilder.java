package com.kaylerrenslow.a3plugin.lang.header.codeStyle.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 03/18/2016.
 */
public class HeaderFormattingBuilder implements FormattingModelBuilder{
	@NotNull
	@Override
	public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
		return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(), new HeaderBlock(element.getNode(), Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(), createSpaceBuilder(settings)), settings);
	}

	private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
		return new SpacingBuilder(settings, HeaderLanguage.INSTANCE);
	}

	@Nullable
	@Override
	public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
		return null;
	}
}
