package com.kaylerrenslow.armaplugin.lang.sqf.psi.codestyle;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 09/11/2017
 */
public class SQFMagicVarColorizerAnnotator implements Annotator {

	@Override
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
		if (element instanceof SQFVariable) {
			SQFVariable var = (SQFVariable) element;
			if (var.isMagicVar()) {
				Annotation a = holder.createInfoAnnotation(TextRange.from(element.getTextOffset(), var.getTextLength()), null);
				a.setTextAttributes(SQFSyntaxHighlighter.MAGIC_VAR);
			}
		}
	}
}
