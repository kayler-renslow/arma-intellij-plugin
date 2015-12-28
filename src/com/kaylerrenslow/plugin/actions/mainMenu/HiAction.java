package com.kaylerrenslow.plugin.actions.mainMenu;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kaylerrenslow.plugin.Static;

import javax.swing.*;

/**
 * Created by Kayler on 12/27/2015.
 */
public class HiAction extends AnAction{
	public HiAction() {
		super(Static.ICON_FILE);
	}

	@Override
	public void actionPerformed(AnActionEvent e) {
		System.out.println("HiAction action performed");
	}
}
