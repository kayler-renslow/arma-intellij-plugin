package com.kaylerrenslow.armaplugin.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.kaylerrenslow.armaplugin.ArmaPlugin;
import com.kaylerrenslow.armaplugin.ArmaPluginIcons;
import com.kaylerrenslow.armaplugin.ArmaTools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.ResourceBundle;

/**
 * @author Kayler
 * @since 10/24/2017
 */
public class ArmaPluginSettingsForm {
	private JPanel panelRoot;
	private JButton btnTestDir;
	private JPanel panelForTfArmaToolsDir;
	private JTextField tfArmaToolsDir;

	public ArmaPluginSettingsForm() {
		btnTestDir.addActionListener(e -> {
			boolean validAToolsDirectory = false;
			if (tfArmaToolsDir.getText() != null) {
				validAToolsDirectory = ArmaTools.isValidA3ToolsDirectory(new File(tfArmaToolsDir.getText()));
			}
			new ArmaToolsDirOkDialog(validAToolsDirectory).show();

		});
	}

	@NotNull
	public JPanel getPanelRoot() {
		return panelRoot;
	}

	private void createUIComponents() {
		tfArmaToolsDir = new JTextField(40);
		{
			panelForTfArmaToolsDir = new TextFieldWithBrowseButton(tfArmaToolsDir, e -> {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.showDialog(panelRoot, ArmaPlugin.getPluginBundle().getString("Dialog.ArmaToolsConfig.select"));
				File file = fc.getSelectedFile();

				if (file == null) {
					return;
				}
				tfArmaToolsDir.setText(file.getAbsolutePath());
			});
		}
	}

	@NotNull
	public String getArmaToolsDirectoryPath() {
		return tfArmaToolsDir.getText() == null ? "" : tfArmaToolsDir.getText();
	}

	public void initArma3ToolsDirectory(@NotNull String path) {
		tfArmaToolsDir.setText(path);
	}

	private class ArmaToolsDirOkDialog extends DialogWrapper {

		private final boolean validArmaToolsDirectory;
		private final ResourceBundle b = ArmaPlugin.getPluginBundle();

		public ArmaToolsDirOkDialog(boolean validArmaToolsDirectory) {
			super(false);
			this.validArmaToolsDirectory = validArmaToolsDirectory;

			init();
			setTitle(b.getString("Misc.aip-notification"));
		}

		@Nullable
		@Override
		protected JComponent createCenterPanel() {
			JPanel root = new JPanel();
			root.add(new JLabel(
					validArmaToolsDirectory ?
									ArmaPluginIcons.ICON_DIALOG_GOOD :
									ArmaPluginIcons.ICON_DIALOG_ERROR
					)
			);
			root.add(
					new JLabel(
							validArmaToolsDirectory ? b.getString("Dialog.ArmaToolsConfig.directory-is-valid")
									: b.getString("Dialog.ArmaToolsConfig.directory-is-not-valid")
					)

			);
			return root;
		}
	}
}
