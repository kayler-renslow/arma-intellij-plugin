package com.kaylerrenslow.a3plugin.dialog.actions.mainMenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kaylerrenslow.a3plugin.dialog.Dialog_About;

/**
 * @author Kayler
 *         This action is called when the About menu button is pressed uner Arma Plugin
 *         Created on 12/27/2015.
 */
public class Action_About extends AnAction {
	public Action_About() {
		super();
	}

	@Override
	public void actionPerformed(AnActionEvent e) {
		Dialog_About d = new Dialog_About(e.getProject(), false);
		d.show();
	}
}
