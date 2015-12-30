package com.kaylerrenslow.plugin.dialog.actions.mainMenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kaylerrenslow.plugin.dialog.Dialog_About;

/**
 * Created by Kayler on 12/27/2015.
 */
public class Action_About extends AnAction{
	public Action_About() {
		super();
	}

	@Override
	public void actionPerformed(AnActionEvent e) {
		Dialog_About d = new Dialog_About(e.getProject(), false);
		d.show();
	}
}
