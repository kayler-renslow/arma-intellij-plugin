package com.kaylerrenslow.armaplugin.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.kaylerrenslow.armaDialogCreator.util.Reference;
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
	private final Reference<String> armaToolsDirectoryRef = new Reference<>();

	public ArmaPluginSettingsForm() {
		btnTestDir.addActionListener(e -> {
			boolean validA3ToolsDirectory = false;
			if (armaToolsDirectoryRef.getValue() != null) {
				validA3ToolsDirectory = ArmaTools.isValidA3ToolsDirectory(new File(armaToolsDirectoryRef.getValue()));
			}
			new ArmaToolsDirOkDialog(validA3ToolsDirectory).show();

		});
	}

	@NotNull
	public JPanel getPanelRoot() {
		return panelRoot;
	}

	private void createUIComponents() {
		final JTextField tfArmaToolsDir = new JTextField(40);
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
				armaToolsDirectoryRef.setValue(tfArmaToolsDir.getText());
			});
		}
	}

	private class ArmaToolsDirOkDialog extends DialogWrapper {

		private final boolean validA3ToolsDirectory;
		private final ResourceBundle b = ArmaPlugin.getPluginBundle();

		public ArmaToolsDirOkDialog(boolean validA3ToolsDirectory) {
			super(false);
			this.validA3ToolsDirectory = validA3ToolsDirectory;

			init();
			setTitle(b.getString("Misc.aip-notification"));
		}

		@Nullable
		@Override
		protected JComponent createCenterPanel() {
			JPanel root = new JPanel();
			root.add(new JLabel(
							validA3ToolsDirectory ?
									ArmaPluginIcons.ICON_DIALOG_GOOD :
									ArmaPluginIcons.ICON_DIALOG_ERROR
					)
			);
			root.add(
					new JLabel(
							validA3ToolsDirectory ? b.getString("Dialog.ArmaToolsConfig.directory-is-valid")
									: b.getString("Dialog.ArmaToolsConfig.directory-is-not-valid")
					)

			);
			return root;
		}
	}
}
