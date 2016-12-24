package com.kaylerrenslow.a3plugin.lang.shared;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.sqf.codeStyle.highlighting.SQFSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * From Intellij doc:
 * <blockquote>
 * "The ExternalAnnotator highlighting has the lowest priority and is invoked only after all other background processing has completed.
 * It uses the same AnnotationHolder interface for converting the output of the external tool into editor highlighting."
 * </blockquote>
 *
 * @author Kayler
 * @since 03/31/2016
 */
public class SharedExternalAnnotator extends ExternalAnnotator {

	private enum Information {
		FILE_ERROR
	}

	@Nullable
	@Override
	public Object collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
		if (hasErrors) {
			if (Plugin.pluginProps.propertyIsTrue(Plugin.UserPropertiesKey.SHOW_SCRIPT_ERRORS)) {
				return new AnnotationInformation(Information.FILE_ERROR, "There is an error.");
			}
		}
		return null;
	}

	@Nullable
	@Override
	public Object collectInformation(@NotNull PsiFile file) {
		return null;
	}

	@Nullable
	@Override
	public Object doAnnotate(Object collectedInfo) {
		return collectedInfo;
	}

	@Override
	public void apply(@NotNull PsiFile file, Object annotationResult, @NotNull AnnotationHolder holder) {
		if (annotationResult instanceof AnnotationInformation) {
			AnnotationInformation information = (AnnotationInformation) annotationResult;
			if (information.type == Information.FILE_ERROR) {
				PsiErrorElement errorElement = PsiUtil.findFirstDescendantElement(file, PsiErrorElement.class);
				if (errorElement == null) {
					Annotation a = holder.createErrorAnnotation(file, information.message);
					a.setFileLevelAnnotation(true);
					return;
				}
				Annotation a = holder.createErrorAnnotation(file, information.message + " " + errorElement.getErrorDescription() + ". Error line number: " + (file.getViewProvider().getDocument().getLineNumber(errorElement.getTextOffset()) + 1));
				a.setTextAttributes(SQFSyntaxHighlighter.BAD_CHARACTER);
				a.setFileLevelAnnotation(true);
			}
		}
	}

	private class AnnotationInformation {
		public final Information type;
		public final String message;

		public AnnotationInformation(Information type, String message) {
			this.type = type;
			this.message = message;
		}
	}
}
