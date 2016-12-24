package com.kaylerrenslow.a3plugin.lang.header.codeStyle;

import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;

/**
 * Code style UI panel for Header langauge. This ui is for configuring formatting options.
 *
 * @author Kayler
 * @since 03/18/2016
 */
public class HeaderCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {
	public HeaderCodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
		super(HeaderLanguage.INSTANCE, currentSettings, settings);
	}
}
