package com.kaylerrenslow.armaplugin.settings;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Kayler
 * @since 11/05/2017
 */
public class ArmaAddonsSettingsForm {
	private JPanel panelRoot;
	private TextFieldWithBrowseButton tfWithBrowseReferenceDirectory;

	private void createUIComponents() {
		tfWithBrowseReferenceDirectory = new TextFieldWithBrowseButton(new JTextField(40));
	}

	@NotNull
	public JComponent getPanelRoot() {
		return panelRoot;
	}
}
