package com.kaylerrenslow.armaplugin.lang;

import com.intellij.codeInsight.documentation.DocumentationManagerProtocol;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFDocumentationProvider;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFParserDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kayler
 * @since 09/06/2017
 */
public class DocumentationUtil {

	/**
	 * Annotates the given comment so that tags like @command, @bis, and @fnc (Arma Intellij Plugin specific tags) are properly annotated
	 *
	 * @param annotator the annotator
	 * @param comment   the comment
	 */
	public static void annotateDocumentationWithArmaPluginTags(@NotNull AnnotationHolder annotator, @NotNull PsiComment comment) {
		List<String> allowedTags = new ArrayList<>(3);
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

	/**
	 * Places the text inside a pre tag to keep formatting. Also inserts documentation window links
	 *
	 * @param docString documentation text
	 * @return reformatted string
	 */
	public static String purtify(String docString) {
		docString = "<pre>" + docString + "</pre>";
		docString = insertCommandLinks(docString);
		docString = insertBisLinks(docString);
		docString = insertFunctionLinks(docString);
		return docString;
	}

	private static String insertCommandLinks(String docString) {
		String commandRegex = "@command ([a-zA-Z_0-9]+)";
		return docString.replaceAll(commandRegex, "<a href='" + DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL + SQFDocumentationProvider.DOC_LINK_PREFIX_COMMAND + "$1'>$1</a>");
	}

	private static String insertBisLinks(String docString) {
		String commandRegex = "@bis ([a-zA-Z_0-9]+)";
		return docString.replaceAll(commandRegex, "<a href='" + DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL + SQFDocumentationProvider.DOC_LINK_PREFIX_BIS_FUNCTION + "$1'>$1</a>");
	}

	private static String insertFunctionLinks(String docString) {
		String commandRegex = "@fnc ([a-zA-Z_0-9]+)";
		return docString.replaceAll(commandRegex, "<a href='" + DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL + SQFDocumentationProvider.DOC_LINK_PREFIX_USER_FUNCTION + "$1'>$1</a>");
	}

	@NotNull
	public static String getCommentContent(@NotNull PsiComment comment) {
		if (comment.getNode().getElementType() == SQFParserDefinition.INLINE_COMMENT) {
			if (comment.getText().length() <= 2) {
				return "";
			}
			return comment.getText().substring(2);
		}
		return comment.getText().substring(2, comment.getTextLength() - 2).replaceAll("\t([^\r\n])", "$1"); //shift comments left 1 tab if tabbed
	}
}
