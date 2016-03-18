package com.kaylerrenslow.a3plugin.lang.header.codeStyle;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.kaylerrenslow.a3plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 03/18/2016.
 */
public class HeaderCodeStyleSettingsProvider extends CodeStyleSettingsProvider{
	private static final String DISPLAY_NAME = Plugin.resources.getString("lang.header.code_style_settings.display_name");

	@NotNull
	@Override
	public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
		return new CodeStyleAbstractConfigurable(settings, originalSettings, DISPLAY_NAME){
			@Override
			protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
				return new HeaderCodeStyleMainPanel(getCurrentSettings(), settings);
			}

			@Nullable
			@Override
			public String getHelpTopic() {
				return null;
			}
		};
	}

	@Nullable
	@Override
	public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
		return new HeaderCodeStyleSettings(settings);
	}

	@Nullable
	@Override
	public String getConfigurableDisplayName() {
		return DISPLAY_NAME;
	}
}
