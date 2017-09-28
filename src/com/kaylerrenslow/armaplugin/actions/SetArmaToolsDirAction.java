package com.kaylerrenslow.armaplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

/**
 * @author Kayler
 * @since 09/27/2017
 */
public class SetArmaToolsDirAction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		//todo finish this method

		//todo have popup that says whether or not the directory is valid
	}
}
