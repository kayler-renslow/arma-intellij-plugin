package com.kaylerrenslow.armaplugin.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * @author Kayler
 * @since 11/05/2017
 */
public class ArmaPluginProjectConfigurable implements SearchableConfigurable {

	private final ArmaAddonsSettingsForm form = new ArmaAddonsSettingsForm();

	@Nls
	@Override
	public String getDisplayName() {
		return "Arma Addons";
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

	@NotNull
	@Override
	public String getId() {
		return "Arma Addons";
	}

	@Nullable
	@Override
	public Runnable enableSearch(String option) {
		return null;
	}
}
