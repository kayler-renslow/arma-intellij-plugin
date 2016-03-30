package com.kaylerrenslow.a3plugin.lang.header.codeStyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.kaylerrenslow.a3plugin.lang.shared.formatting.CodeStyleUtil;

/**
 * @author Kayler
 * CustomCodeStyleSettings implementation for Header language
 * Created on 03/18/2016.
 */
public class HeaderCodeStyleSettings extends CustomCodeStyleSettings{
	public HeaderCodeStyleSettings(CodeStyleSettings settings) {
		super("HeaderCodeStyleSettings", settings);
		setDefaults(settings);
	}

	private void setDefaults(CodeStyleSettings settings) {
		CodeStyleUtil.ClassBraceStyle.setClassBraceStyle(settings, CodeStyleSettings.NEXT_LINE);
		settings.SPACE_AROUND_ADDITIVE_OPERATORS = false;
		settings.SPACE_AROUND_ASSIGNMENT_OPERATORS = false;
		settings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS = false;
		settings.SPACE_BEFORE_COMMA = false;
		settings.SPACE_AFTER_COMMA = true;
		settings.SPACE_BEFORE_COLON = true;
		settings.SPACE_AFTER_COLON = true;
		settings.SPACE_BEFORE_CLASS_LBRACE = true;
		settings.KEEP_LINE_BREAKS = true;
	}

}
