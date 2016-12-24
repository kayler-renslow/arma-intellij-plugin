package com.kaylerrenslow.a3plugin.dialog.actions.mainMenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kaylerrenslow.a3plugin.dialog.Dialog_PluginProperties;

/**
 * This action is called when Plugin Properties menu option is clicked from Arma Plugin menu
 * @author Kayler
 * @since 12/27/2015
 */
public class Action_PluginProperties extends AnAction{
	public Action_PluginProperties() {
		super();
	}

	@Override
	public void actionPerformed(AnActionEvent e) {
		Dialog_PluginProperties d = new Dialog_PluginProperties(e.getProject(), false);
		d.show();
	}
}
