package com.kaylerrenslow.armaplugin.lang.sqf;

import com.intellij.lang.Language;

/**
 * Language extension point for SQF language
 *
 * @author Kayler
 * @since 10/31/2015
 */
public class SQFLanguage extends Language {
	public static final SQFLanguage INSTANCE = new SQFLanguage();

	public SQFLanguage() {
		super("Arma.SQF");
	}

}
