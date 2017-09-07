package com.kaylerrenslow.armaplugin.lang;

import com.intellij.codeInsight.documentation.DocumentationManagerProtocol;
import com.intellij.psi.PsiComment;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFDocumentationProvider;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFParserDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 09/06/2017
 */
public class DocumentationUtil {
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
