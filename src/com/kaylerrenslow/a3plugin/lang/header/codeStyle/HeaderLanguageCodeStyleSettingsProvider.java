package com.kaylerrenslow.a3plugin.lang.header.codeStyle;

import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;
import com.kaylerrenslow.a3plugin.lang.header.HeaderStatic;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Kayler on 03/18/2016.
 */
public class HeaderLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider{
	@NotNull
	@Override
	public Language getLanguage() {
		return HeaderLanguage.INSTANCE;
	}

	@Override
	public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
		switch (settingsType){
			case BLANK_LINES_SETTINGS:

				break;
			case COMMENTER_SETTINGS:
					break;
			case INDENT_SETTINGS:
					break;
			case LANGUAGE_SPECIFIC:
					break;
			case SPACING_SETTINGS:
					break;
		}
	}

	@Override
	public String getCodeSample(@NotNull SettingsType settingsType) {
		return HeaderStatic.HEADER_SAMPLE_CODE_TEXT;
	}
}
