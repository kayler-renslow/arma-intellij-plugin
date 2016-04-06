package com.kaylerrenslow.a3plugin.dialog.actions.newGroup;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kaylerrenslow.a3plugin.dialog.actions.SimpleGuiAction;

/**
 * Created by Kayler on 04/05/2016.
 */
public class Action_NewSQFFile extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		Dialog_NewSQFFile.showNewInstance(new CreateNewSQFFile());
	}

	private class CreateNewSQFFile implements SimpleGuiAction<String> {

		@Override
		public void actionPerformed(String fileName) {
			System.err.println(fileName);
		}
	}
}
