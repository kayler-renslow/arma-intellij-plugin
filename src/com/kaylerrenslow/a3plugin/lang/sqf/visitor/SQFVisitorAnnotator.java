package com.kaylerrenslow.a3plugin.lang.sqf.visitor;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.sqf.codeStyle.highlighting.SQFSyntaxHighlighter;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 @author Kayler
 AST visistor implementation for annotating with SQFAnnotator
 Created on 03/16/2016. */
public class SQFVisitorAnnotator extends SQFVisitor {

	private AnnotationHolder annotator;

	private Annotation expect(String expected, PsiElement got) {
		return annotator.createErrorAnnotation(got, String.format(Plugin.resources.getString("lang.sqf.annotator.error.expected_f"), expected, got.getText()));
	}

	private Annotation createDeleteTokenAnotation(PsiElement element) {
		return annotator.createErrorAnnotation(element, String.format(Plugin.resources.getString("lang.sqf.annotator.error.unexpected_f"), element.getText()));
	}


	public SQFVisitorAnnotator(AnnotationHolder annotator) {
		this.annotator = annotator;
	}

	@Override
	public void visitComment(PsiComment comment) {
		super.visitComment(comment);
		String commentContent = SQFPsiUtil.getCommentContent(comment);
		String rawCommentText = comment.getText();
		if (commentContent.toLowerCase().startsWith("note")) {
			Annotation a = annotator.createAnnotation(HighlightSeverity.INFORMATION, TextRange.from(comment.getTextOffset() + 2, commentContent.length()), commentContent);
			a.setTextAttributes(SQFSyntaxHighlighter.COMMENT_NOTE);
		}
		//annotate tags
		ArrayList<String> allowedTags = new ArrayList<>(3);
		allowedTags.add("command");
		allowedTags.add("bis");
		allowedTags.add("fnc");
		Pattern patternTag = Pattern.compile("@([a-zA-Z]+) ([a-zA-Z_0-9]+)");
		Matcher matcher = patternTag.matcher(rawCommentText);
		String tag;
		Annotation annotation;
		int startTag, endTag, startArg, endArg;
		while (matcher.find()) {
			if (matcher.groupCount() < 2) {
				continue;
			}
			tag = matcher.group(1);
			if (!allowedTags.contains(tag)) {
				continue;
			}
			startTag = matcher.start(1);
			endTag = matcher.end(1);
			startArg = matcher.start(2);
			endArg = matcher.end(2);
			annotation = annotator.createAnnotation(HighlightSeverity.INFORMATION, TextRange.create(comment.getTextOffset() + startTag - 1, comment.getTextOffset() + endTag), null);
			annotation.setTextAttributes(DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);
			annotation = annotator.createAnnotation(HighlightSeverity.INFORMATION, TextRange.create(comment.getTextOffset() + startArg, comment.getTextOffset() + endArg), null);
			annotation.setTextAttributes(DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);
		}

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
				expect("String or Array of Strings", postfix);
			}
		} else if (commandName.equals("params")) {

		}
		super.visitCommandExpression(o);
	}

	@Override
	public void visitAssignment(@NotNull SQFAssignment o) {
		if (o.getCommand() != null) {
			if (!o.getCommand().getText().equals("private")) {
				expect("private", o.getCommand());
			}
		}
		super.visitAssignment(o);
	}

	@Override
	public void visitCaseStatement(@NotNull SQFCaseStatement o) {
		super.visitCaseStatement(o);
		if (!o.getCommand().getText().equals("case")) {
			expect("case", o.getCommand());
		}
	}


}

