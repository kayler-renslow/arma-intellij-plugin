package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * Annotator implementation for SQF language
 * Created on 03/15/2016.
 */
public class SQFAnnotator implements Annotator{

	private final SQFVisitorAnnotator visitorAnnotator = new SQFVisitorAnnotator();

	@Override
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
		visitorAnnotator.setAnnotator(holder);
		element.accept(visitorAnnotator);
	}
}