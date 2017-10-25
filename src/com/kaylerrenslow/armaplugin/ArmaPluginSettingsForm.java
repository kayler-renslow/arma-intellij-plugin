package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.kaylerrenslow.armaDialogCreator.util.Reference;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

/**
 * @author Kayler
 * @since 10/24/2017
 */
public class ArmaPluginSettingsForm {
	private JPanel panelRoot;
	private JButton btnTestDir;
	private JPanel panelForTfArmaToolsDir;
	private final Reference<String> armaToolsDirectory = new Reference<>();

	public ArmaPluginSettingsForm() {
		btnTestDir.addActionListener(e -> {
			boolean validA3ToolsDirectory = false;
			if (armaToolsDirectory.getValue() != null) {
				validA3ToolsDirectory = ArmaTools.isValidA3ToolsDirectory(new File(armaToolsDirectory.getValue()));
			}
			System.out.println("ArmaPluginSettingsForm.ArmaPluginSettingsForm validA3ToolsDirectory=" + validA3ToolsDirectory);
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
				armaToolsDirectory.setValue(tfArmaToolsDir.getText());
			});
		}
	}


}
