package com.kaylerrenslow.armaplugin.lang;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Annotator used for tags like @fnc, @bis, and @fnc, all of which are Arma Intellij Plugin specific
 *
 * @author Kayler
 * @since 09/11/2017
 */
public class DocumentationTagsAnnotator implements Annotator {

	@Override
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
		if (element instanceof PsiComment) {
			DocumentationUtil.annotateDocumentationWithArmaPluginTags(holder, (PsiComment) element);
		}
	}
}
