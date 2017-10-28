package com.kaylerrenslow.armaplugin;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.kaylerrenslow.armaplugin.dialog.ArmaPluginSettingsForm;
import com.kaylerrenslow.armaplugin.dialog.IndexArmaAddonsStatusDialog;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JComponent;
import java.io.File;

/**
 * @author Kayler
 * @since 09/28/2017
 */
public class ArmaPluginProjectConfigurable implements Configurable {

	private final ArmaPluginSettingsForm form = new ArmaPluginSettingsForm();

	@Nls
	@Override
	public String getDisplayName() {
		return "Arma Plugin";
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		File a3ToolsDir = ArmaPluginUserData.getInstance().getArmaToolsDirectory();
		form.initArma3ToolsDirectory(a3ToolsDir == null ? "" : a3ToolsDir.getAbsolutePath());

		//testing code
		JButton btnTest = new JButton("Test");
		form.getPanelRoot().add(btnTest);
		btnTest.addActionListener(e -> {
			DataContext dc = DataManager.getInstance().getDataContextFromFocus().getResult();
			Project project = dc.getData(DataKeys.PROJECT);
			if (project == null) {
				throw new IllegalStateException("project is null");
			}

			new IndexArmaAddonsStatusDialog(project).setVisible(true);
		});
		//end testing code

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
}
