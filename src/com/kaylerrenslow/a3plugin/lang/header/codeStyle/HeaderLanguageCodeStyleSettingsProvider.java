package com.kaylerrenslow.a3plugin.lang.header.codeStyle;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.intellij.util.containers.ArrayListSet;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;
import com.kaylerrenslow.a3plugin.lang.header.HeaderStatic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author Kayler
 * Created on 03/18/2016.
 */
public class HeaderLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider{

	@NotNull
	@Override
	public Language getLanguage() {
		return HeaderLanguage.INSTANCE;
	}

	@Nullable
	@Override
	public IndentOptionsEditor getIndentOptionsEditor() {
		return new IndentOptionsEditor();
	}

	@Override
	public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
		switch (settingsType) {
			case BLANK_LINES_SETTINGS:
				consumer.showStandardOptions("KEEP_BLANK_LINES_BEFORE_RBRACE", "KEEP_BLANK_LINES_IN_CODE");
				break;
			case COMMENTER_SETTINGS:
				consumer.showAllStandardOptions();
				break;
			case INDENT_SETTINGS:
				consumer.showStandardOptions();
				break;
			case LANGUAGE_SPECIFIC:
				consumer.showAllStandardOptions();
				break;
			case SPACING_SETTINGS:
				consumer.showStandardOptions("SPACE_AROUND_ADDITIVE_OPERATORS", "SPACE_AROUND_ASSIGNMENT_OPERATORS", "SPACE_AROUND_MULTIPLICATIVE_OPERATORS", "SPACE_AFTER_SEMICOLON", "SPACE_AFTER_COMMA", "SPACE_BEFORE_COMMA");
				consumer.showStandardOptions("SPACE_WITHIN_ARRAY_INITIALIZER_BRACES", "SPACE_BEFORE_CLASS_LBRACE");

				consumer.showStandardOptions("SPACE_BEFORE_COLON", "SPACE_AFTER_COLON");
				consumer.moveStandardOption("SPACE_BEFORE_COLON", "Other");
				consumer.moveStandardOption("SPACE_AFTER_COLON", "Other");
				break;
			case WRAPPING_AND_BRACES_SETTINGS:
				consumer.showStandardOptions("WRAPPING_COMMENTS","KEEP_LINE_BREAKS", "CLASS_BRACE_STYLE", "ARRAY_INITIALIZER_WRAP", "ARRAY_INITIALIZER_LBRACE_ON_NEXT_LINE", "ARRAY_INITIALIZER_RBRACE_ON_NEXT_LINE");
				break;
		}
	}

	@Override
	public String getCodeSample(@NotNull SettingsType settingsType) {
		return HeaderStatic.HEADER_SAMPLE_CODE_TEXT;
	}
}
