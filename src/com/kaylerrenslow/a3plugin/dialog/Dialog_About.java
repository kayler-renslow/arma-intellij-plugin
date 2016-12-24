package com.kaylerrenslow.a3plugin.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.kaylerrenslow.a3plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Dialog for About the plugin
 *
 * @author Kayler
 * @since 12/30/2015
 */
public class Dialog_About extends DialogWrapper {
	private static final String TITLE = "About the Arma Intellij Plugin";
	private static final String TEXT = "";

	public Dialog_About(@Nullable Project project, boolean canBeParent) {
		super(project, canBeParent);
		init();
		setTitle(TITLE);
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		JPanel panel = new JPanel();
		panel.add(new JLabel("Plugin version: " + Plugin.VERSION));
		return panel;
	}

}
