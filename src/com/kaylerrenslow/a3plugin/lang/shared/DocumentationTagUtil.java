package com.kaylerrenslow.a3plugin.lang.shared;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 Created by Kayler on 06/07/2016.
 */
public class DocumentationTagUtil {
	public static void annotateDocumentation(AnnotationHolder annotator, PsiComment comment) {
		ArrayList<String> allowedTags = new ArrayList<>(3);
		allowedTags.add("command");
		allowedTags.add("bis");
		allowedTags.add("fnc");
		Pattern patternTag = Pattern.compile("@([a-zA-Z]+) ([a-zA-Z_0-9]+)");
		Matcher matcher = patternTag.matcher(comment.getText());
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
}
