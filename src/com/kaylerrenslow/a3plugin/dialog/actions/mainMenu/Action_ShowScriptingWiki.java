package com.kaylerrenslow.a3plugin.dialog.actions.mainMenu;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kaylerrenslow.a3plugin.Plugin;

/**
 * This action is invoked when show command scripting wiki menu button under Arma Plugin is pressed
 * @author Kayler
 * @since 03/18/2016
 */
public class Action_ShowScriptingWiki extends AnAction{
	@Override
	public void actionPerformed(AnActionEvent e) {
		BrowserUtil.browse(Plugin.resources.getString("plugin.action.wiki_link_commands"));
	}
}
