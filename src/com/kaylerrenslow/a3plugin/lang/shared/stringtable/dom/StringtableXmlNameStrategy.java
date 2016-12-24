package com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom;

import com.intellij.util.xml.DomNameStrategy;

/**
 * @author Kayler
 * @since 04/09/2016
 */
public class StringtableXmlNameStrategy extends DomNameStrategy {


	@Override
	public String convertName(String propertyName) {
		return capitalize(propertyName);
	}

	@Override
	public String splitIntoWords(String xmlElementName) {
		return xmlElementName;
	}

	public static String capitalize(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		if (name.length() > 1 && Character.isUpperCase(name.charAt(0))) {
			return name;
		}
		char chars[] = name.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}
}
