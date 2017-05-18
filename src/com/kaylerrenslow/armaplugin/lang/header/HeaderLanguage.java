package com.kaylerrenslow.armaplugin.lang.header;

import com.intellij.lang.Language;

/**
 * Language extension point for Header language
 *
 * @author Kayler
 * @since 10/31/2015
 */
public class HeaderLanguage extends Language {
	public static final HeaderLanguage INSTANCE = new HeaderLanguage();

	public HeaderLanguage() {
		super("Arma.Header");
	}


}
