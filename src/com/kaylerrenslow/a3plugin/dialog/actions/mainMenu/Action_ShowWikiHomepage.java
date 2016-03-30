package com.kaylerrenslow.a3plugin.dialog.actions.mainMenu;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kaylerrenslow.a3plugin.Plugin;

/**
 * @author Kayler
 * This action is called when show wiki homepage button is pressed under Arma Plugin
 * Created on 03/18/2016.
 */
public class Action_ShowWikiHomepage extends AnAction{
	@Override
	public void actionPerformed(AnActionEvent e) {
		BrowserUtil.browse(Plugin.resources.getString("plugin.action.wiki_link_main_page"));
	}
}
