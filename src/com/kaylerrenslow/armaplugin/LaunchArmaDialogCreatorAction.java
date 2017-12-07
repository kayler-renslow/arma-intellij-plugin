package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.kaylerrenslow.armaDialogCreator.main.ArmaDialogCreator;

/**
 * @author Kayler
 * @since 12/07/2017
 */
public class LaunchArmaDialogCreatorAction extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		try {
			ArmaDialogCreator.main(new String[]{});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
