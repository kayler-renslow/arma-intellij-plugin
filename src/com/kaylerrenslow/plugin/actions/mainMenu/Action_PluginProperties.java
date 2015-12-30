package com.kaylerrenslow.plugin.actions.mainMenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kaylerrenslow.plugin.dialog.Dialog_PluginProperties;

/**
 * Created by Kayler on 12/27/2015.
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
