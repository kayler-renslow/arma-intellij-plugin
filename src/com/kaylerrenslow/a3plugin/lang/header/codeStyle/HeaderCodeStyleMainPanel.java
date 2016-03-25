package com.kaylerrenslow.a3plugin.lang.header.codeStyle;

import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;

/**
 * Created by Kayler on 03/18/2016.
 */
public class HeaderCodeStyleMainPanel extends TabbedLanguageCodeStylePanel{
	public HeaderCodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
		super(HeaderLanguage.INSTANCE, currentSettings, settings);
		this.addIndentOptionsTab(settings);
	}


	@Override
	protected void addWrappingAndBracesTab(CodeStyleSettings settings) {
		super.addWrappingAndBracesTab(settings);
	}
}
