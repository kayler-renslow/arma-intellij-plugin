package com.kaylerrenslow.armaplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kaylerrenslow.armaplugin.ArmaAddonsManager;
import com.kaylerrenslow.armaplugin.ArmaAddonsProjectConfig;

import javax.swing.*;
import java.io.File;

/**
 * @author Kayler
 * @since 09/27/2017
 */
public class TestAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		if (anActionEvent.getProject() == null) {
			return;
		}
		JFileChooser chooser = new JFileChooser();
		chooser.showDialog(null, "");
		File selectedFile = chooser.getSelectedFile();
		if (selectedFile != null) {
			ArmaAddonsProjectConfig config = ArmaAddonsManager.parseAddonsConfig(selectedFile, anActionEvent.getProject());
			ArmaAddonsManager.getAddonsManagerInstance().loadAddons(config);
			System.out.println(ArmaAddonsManager.getAddonsManagerInstance().getAddons());
		}
	}
}
