package com.kaylerrenslow.a3plugin.dialog.actions.newGroup;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.kaylerrenslow.a3plugin.dialog.Dialog_NewSQFFunction;
import com.kaylerrenslow.a3plugin.dialog.actions.SimpleGuiAction;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;

import java.io.IOException;

/**
 * Created by Kayler on 04/05/2016.
 */
public class Action_NewSQFFunction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent e) {
		Dialog_NewSQFFunction.showNewInstance(e, new CreateNewSQFFunction(e.getProject()));
	}

	private class CreateNewSQFFunction implements SimpleGuiAction<Dialog_NewSQFFunction.NewSQFFunctionActionPerformedData> {

		private final Project project;

		CreateNewSQFFunction(Project project) {
			this.project = project;
		}

		@Override
		public void actionPerformed(Dialog_NewSQFFunction.NewSQFFunctionActionPerformedData data) {
			WriteCommandAction.runWriteCommandAction(project, new Runnable() {
				@Override
				public void run() {
					System.out.println(data);
					String functionTagName = data.functionTagName;
					String functionClassName = data.functionClassName;

					if(1 == 1){
						return;
					}
					try {
						VirtualFile directory = null;
						VirtualFile created = directory.createChildData(null, null);
					} catch (IOException e) {
						e.printStackTrace(System.out);
						return;
					}
				}
			});
		}
	}
}
