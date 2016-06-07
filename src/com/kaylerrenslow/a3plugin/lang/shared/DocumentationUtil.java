package com.kaylerrenslow.a3plugin.lang.shared;

import com.intellij.codeInsight.documentation.DocumentationManagerProtocol;
import com.kaylerrenslow.a3plugin.lang.sqf.providers.SQFDocumentationProvider;

/**
 @author Kayler
 Various Documentation utilities
 Created on 04/02/2016. */
public class DocumentationUtil {
	/**
	 Places the text inside a pre tag to keep formatting

	 @param docString documentation text
	 @return reformatted string
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
}
