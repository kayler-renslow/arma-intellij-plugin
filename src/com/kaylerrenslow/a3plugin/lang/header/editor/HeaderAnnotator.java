package com.kaylerrenslow.a3plugin.lang.header.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 04/02/2016
 */
public class HeaderAnnotator implements Annotator {

	private final HeaderAnnotatorVisitor visitor = new HeaderAnnotatorVisitor();

	@Override
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
		visitor.setAnnotationHolder(holder);
		element.accept(visitor);
	}
}
