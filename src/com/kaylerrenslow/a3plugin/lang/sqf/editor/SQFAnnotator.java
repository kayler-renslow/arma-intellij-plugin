package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.shared.DocumentationTagUtil;
import com.kaylerrenslow.a3plugin.lang.shared.DocumentationUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.codeStyle.highlighting.SQFSyntaxHighlighter;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFAssignment;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFCommandExpression;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFLiteralExpression;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Annotator implementation for SQF language
 *
 * @author Kayler
 * @since 03/15/2016
 */
public class SQFAnnotator implements Annotator {

	@Override
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
		element.accept(new SQFVisitorAnnotator(holder));
	}

	/**
	 * AST visitor implementation for annotating with SQFAnnotator
	 *
	 * @author Kayler
	 * @since 03/16/2016
	 */
	private static class SQFVisitorAnnotator extends SQFVisitor {

		private AnnotationHolder annotator;

		static Annotation expect(AnnotationHolder annotator, String expected, PsiElement got) {
			return annotator.createErrorAnnotation(got, String.format(Plugin.resources.getString("lang.sqf.annotator.error.expected_f"), expected, got.getText()));
		}

		static Annotation createDeleteTokenAnotation(AnnotationHolder annotator, PsiElement element) {
			return annotator.createErrorAnnotation(element, String.format(Plugin.resources.getString("lang.sqf.annotator.error.unexpected_f"), element.getText()));
		}

		private Annotation expect(String expected, PsiElement got) {
			return expect(annotator, expected, got);
		}

		private Annotation createDeleteTokenAnotation(PsiElement element) {
			return createDeleteTokenAnotation(annotator, element);
		}


		SQFVisitorAnnotator(AnnotationHolder annotator) {
			this.annotator = annotator;
		}

		@Override
		public void visitComment(PsiComment comment) {
			String commentContent = DocumentationUtil.getCommentContent(comment);
			if (commentContent.toLowerCase().startsWith("note")) {
				Annotation a = annotator.createAnnotation(HighlightSeverity.INFORMATION, TextRange.from(comment.getTextOffset() + 2, commentContent.length()), commentContent);
				a.setTextAttributes(SQFSyntaxHighlighter.COMMENT_NOTE);
			}
			//annotate tags
			DocumentationTagUtil.annotateDocumentation(annotator, comment);
		}

		@Override
		public void visitCommandExpression(@NotNull SQFCommandExpression o) {
			PsiElement prefix = o.getPrefixArgument();
			PsiElement postfix = o.getPostfixArgument();
			String commandName = o.getCommandName();
			if (commandName.equals("private")) {
				boolean error = false;
				if (postfix instanceof SQFLiteralExpression) {
					SQFLiteralExpression literal = (SQFLiteralExpression) postfix;
					if (literal.getArrayVal() == null && literal.getString() == null) {
						error = true;
					}
				} else {
					error = true;
				}
				if (error) {
					if (prefix != null) {
						createDeleteTokenAnotation(prefix);
					}
					if (postfix != null) {
						expect("String or Array of Strings", postfix);
					}
				}
			} else if (commandName.equals("params")) {

			}
		}

		@Override
		public void visitAssignment(@NotNull SQFAssignment o) {
			if (o.getCommand() != null) {
				if (!o.getCommand().getText().equals("private")) {
					expect("private", o.getCommand());
				}
			}
		}


	}


}