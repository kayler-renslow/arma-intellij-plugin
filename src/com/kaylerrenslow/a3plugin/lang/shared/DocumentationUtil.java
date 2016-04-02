package com.kaylerrenslow.a3plugin.lang.shared;

/**
 * @author Kayler
 *         Various Documentation utilities
 *         Created on 04/02/2016.
 */
public class DocumentationUtil {
	/**
	 * Places the text inside a pre tag to keep formatting
	 *
	 * @param docString documentation text
	 * @return reformatted string
	 */
	public static String purtify(String docString) {
		return "<pre>" + docString + "</pre>";
	}
}
