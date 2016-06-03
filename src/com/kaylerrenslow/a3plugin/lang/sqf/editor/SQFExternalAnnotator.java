package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.a3plugin.lang.sqf.visitor.SQFVisitorExternalAnnotator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Created by Kayler on 06/03/2016.
 */
public class SQFExternalAnnotator extends ExternalAnnotator {
	@Nullable
	@Override
	public Object collectInformation(@NotNull PsiFile file) {
		return new Object();
	}

	@Nullable
	@Override
	public Object collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
		return new Object();
	}

	@Nullable
	@Override
	public Object doAnnotate(Object collectedInfo) {
		return new Object();
	}

	@Override
	public void apply(@NotNull PsiFile file, Object annotationResult, @NotNull AnnotationHolder holder) {
		PsiElement[] children= file.getChildren();
		for(PsiElement child : children){
			child.accept(new SQFVisitorExternalAnnotator(holder));
		}
	}
}
