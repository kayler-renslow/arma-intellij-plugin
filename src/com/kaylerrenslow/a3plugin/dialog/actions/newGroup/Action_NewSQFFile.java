package com.kaylerrenslow.a3plugin.dialog.actions.newGroup;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.a3plugin.dialog.Dialog_NewSQFFile;
import com.kaylerrenslow.a3plugin.dialog.actions.SimpleGuiAction;

import java.io.IOException;

/**
 * Created by Kayler on 04/05/2016.
 */
public class Action_NewSQFFile extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		Dialog_NewSQFFile.showNewInstance(e, new CreateNewSQFFile(e.getProject()));
	}

	@Override
	public void update(AnActionEvent e) {
		if(e.getProject() == null){
			e.getInputEvent().consume();
		}
	}

	private class CreateNewSQFFile implements SimpleGuiAction<Pair<String, VirtualFile>> {

		private final Project project;

		CreateNewSQFFile(Project project) {
			this.project = project;
		}

		@Override
		public void actionPerformed(Pair<String, VirtualFile> data) {
			WriteCommandAction.runWriteCommandAction(project, new Runnable() {
				@Override
				public void run() {
					String fileName = data.first;
					if (!fileName.endsWith(".sqf")) {
						fileName = fileName + ".sqf";
					}
					try {
						VirtualFile directory = data.second;
						VirtualFile created = directory.createChildData(null, fileName);
					} catch (IOException e) {
						e.printStackTrace(System.out);
						return;
					}
				}
			});
		}
	}
}
