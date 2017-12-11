package com.kaylerrenslow.armaplugin.lang;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.header.psi.codestyle.HeaderSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 09/11/2017
 */
public class PreprocessorColorizerAnnotator implements Annotator {

	@Override
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
		String elementText = element.getText();
		if (elementText.length() >= 1 && elementText.charAt(0) == '#') {
			int macroNameEnd = elementText.indexOf(' ');
			Annotation a = holder.createInfoAnnotation(TextRange.from(element.getTextOffset(), macroNameEnd < 0 ? elementText.length() : macroNameEnd), null);
			a.setTextAttributes(HeaderSyntaxHighlighter.PREPROCESSOR);
		}
	}
}
