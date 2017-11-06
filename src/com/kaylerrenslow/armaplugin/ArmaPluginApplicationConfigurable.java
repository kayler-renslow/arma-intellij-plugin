package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.io.FileUtil;
import com.kaylerrenslow.armaplugin.dialog.ArmaPluginSettingsForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.io.File;

/**
 * @author Kayler
 * @since 09/28/2017
 */
public class ArmaPluginApplicationConfigurable implements SearchableConfigurable {

	private final ArmaPluginSettingsForm form = new ArmaPluginSettingsForm();

	@Nls
	@Override
	public String getDisplayName() {
		return "Arma Plugin Settings";
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		File a3ToolsDir = ArmaPluginUserData.getInstance().getArmaToolsDirectory();
		form.initArma3ToolsDirectory(a3ToolsDir == null ? "" : a3ToolsDir.getAbsolutePath());
		return form.getPanelRoot();
	}

	@Override
	public boolean isModified() {
		String enteredDir = form.getArmaToolsDirectoryPath();
		boolean modified = false;
		File currentArmaToolsDir = ArmaPluginUserData.getInstance().getArmaToolsDirectory();
		if (currentArmaToolsDir == null && enteredDir.length() == 0) {
			modified = false;
		} else {
			//if modified, that means the files aren't equal
			modified = !FileUtil.filesEqual(new File(enteredDir), ArmaPluginUserData.getInstance().getArmaToolsDirectory());
		}
		return modified;
	}

	@Override
	public void apply() throws ConfigurationException {
		ArmaPluginApplicationSettings.getInstance().getState().armaToolsDirectory = form.getArmaToolsDirectoryPath();
	}

	@NotNull
	@Override
	public String getId() {
		return "Arma Plugin Settings";
	}

	@Nullable
	@Override
	public Runnable enableSearch(String option) {
		return null;
	}
}
