package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Kayler
 * @since 09/28/2017
 */
public class ArmaPluginProjectConfigurable implements Configurable {

	private final JLabel lblCurrentDirPath = new JLabel();
	private final ArmaPluginSettingsForm form = new ArmaPluginSettingsForm();

	@Nls
	@Override
	public String getDisplayName() {
		return "Arma Plugin";
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		return form.getPanelRoot();
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public void apply() throws ConfigurationException {

	}
}
