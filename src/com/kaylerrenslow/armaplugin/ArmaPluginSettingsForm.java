package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import javafx.stage.DirectoryChooser;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

/**
 * @author Kayler
 * @since 10/24/2017
 */
public class ArmaPluginSettingsForm {
	private JPanel panelRoot;
	private JPanel panelArmaToolsCfg;
	private JButton btnTestDir;
	private JPanel panelForTfArmaToolsDir;

	@NotNull
	public JPanel getPanelRoot() {
		return panelRoot;
	}

	private void createUIComponents() {
		JTextField tfArmaToolsDir = new JTextField(20);
		{
			panelForTfArmaToolsDir = new TextFieldWithBrowseButton(tfArmaToolsDir, e -> {
				DirectoryChooser dc = new DirectoryChooser();
				File file = dc.showDialog(null);
				if (file == null) {
					return;
				}
				tfArmaToolsDir.setText(file.getAbsolutePath());
			});
		}
		{
			btnTestDir.addActionListener(e -> {
				boolean validA3ToolsDirectory = ArmaTools.isValidA3ToolsDirectory(new File(tfArmaToolsDir.getText()));
				
			});
		}
	}


}
