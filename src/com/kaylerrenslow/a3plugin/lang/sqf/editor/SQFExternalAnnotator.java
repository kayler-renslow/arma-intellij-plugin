package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.a3plugin.dialog.SimpleMessageDialog;
import com.kaylerrenslow.a3plugin.lang.sqf.visitor.SQFVisitorExternalAnnotator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
		PsiElement[] children = file.getChildren();
		SQFVisitorExternalAnnotator visitor = new SQFVisitorExternalAnnotator();
		for (PsiElement child : children) {
			child.accept(visitor);
		}
		return new VisitorResult(visitor);
	}

	@Nullable
	@Override
	public Object doAnnotate(Object collectedInfo) {
		return collectedInfo;
	}

	@Override
	public void apply(@NotNull PsiFile file, Object annotationResult, @NotNull AnnotationHolder holder) {
		if (annotationResult instanceof VisitorResult) {
			VisitorResult result = (VisitorResult) annotationResult;
			Annotation a = null;
			for (SQFVisitorExternalAnnotator.ToMarkAnnotation toMark : result.annotations) {
				if (toMark instanceof SQFVisitorExternalAnnotator.ToMarkAnnotation.WeakWarningAnnotation) {
					SQFVisitorExternalAnnotator.ToMarkAnnotation.WeakWarningAnnotation weakWarningAnnotation = (SQFVisitorExternalAnnotator.ToMarkAnnotation.WeakWarningAnnotation) toMark;
					a = holder.createWeakWarningAnnotation(weakWarningAnnotation.getVar(), weakWarningAnnotation.getString());
				} else if (toMark instanceof SQFVisitorExternalAnnotator.ToMarkAnnotation.WeakWarningAnnotation2) {
					SQFVisitorExternalAnnotator.ToMarkAnnotation.WeakWarningAnnotation2 warningAnnotation2 = (SQFVisitorExternalAnnotator.ToMarkAnnotation.WeakWarningAnnotation2) toMark;
					a = holder.createWeakWarningAnnotation(warningAnnotation2.getRange(), warningAnnotation2.getString());
				} else if (toMark instanceof SQFVisitorExternalAnnotator.ToMarkAnnotation.WarningAnnotation) {
					SQFVisitorExternalAnnotator.ToMarkAnnotation.WarningAnnotation warningAnnotation = (SQFVisitorExternalAnnotator.ToMarkAnnotation.WarningAnnotation) toMark;
					a = holder.createWarningAnnotation(warningAnnotation.getVar(), warningAnnotation.getString());
				} else if (toMark instanceof SQFVisitorExternalAnnotator.ToMarkAnnotation.Annotation) {
					SQFVisitorExternalAnnotator.ToMarkAnnotation.Annotation ann = (SQFVisitorExternalAnnotator.ToMarkAnnotation.Annotation) toMark;
					a = holder.createAnnotation(ann.getWarning(), ann.getRangeCurrentNode(), ann.getString());
				} else {
					try {
						throw new IllegalStateException("dude. All cases should be taken care of"); //yes I realize this doesn't make any sense. But it will be easier to know that it's broken
					} catch (IllegalStateException ee) {
						ee.printStackTrace();
						SimpleMessageDialog.showNewDialog("Error.", SimpleMessageDialog.getExceptionString(ee)).show();
					}
				}
				if (toMark.myFix != null) {
					a.registerFix(toMark.myFix.getQuickFixAction());
				}
			}
		}

	}

	private static class VisitorResult {
		public final List<SQFVisitorExternalAnnotator.ToMarkAnnotation> annotations;

		public VisitorResult(SQFVisitorExternalAnnotator externalAnnotator) {
			this.annotations = externalAnnotator.toMarkAnnotations;
		}
	}
}
