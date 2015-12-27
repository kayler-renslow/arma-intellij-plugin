package com.kaylerrenslow.plugin.actions.mainMenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by Kayler on 12/27/2015.
 */
public class HiAction extends AnAction{
	@Override
	public void actionPerformed(AnActionEvent e) {
		System.out.println("HiAction action performed");
	}
}
