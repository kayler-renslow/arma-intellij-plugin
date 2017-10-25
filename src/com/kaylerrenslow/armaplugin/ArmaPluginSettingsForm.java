package com.kaylerrenslow.armaplugin;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Kayler
 * @since 10/24/2017
 */
public class ArmaPluginSettingsForm {
	private JPanel panelRoot;
	private JPanel panelArmaToolsCfg;
	private JTextField tfArmaToolsDir;
	private JButton btnLocateArmaTools;
	private JButton btnTestDir;

	@NotNull
	public JPanel getPanelRoot() {
		return panelRoot;
	}
}
