package com.kaylerrenslow.a3plugin.dialog.actions.newGroup;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.dialog.Dialog_NewSQFFile;
import com.kaylerrenslow.a3plugin.dialog.SimpleMessageDialog;
import com.kaylerrenslow.a3plugin.dialog.actions.SimpleGuiAction;
import com.kaylerrenslow.a3plugin.lang.header.exception.DescriptionExtNotDefinedException;
import com.kaylerrenslow.a3plugin.project.ArmaProjectDataManager;
import com.kaylerrenslow.a3plugin.util.PluginUtil;

import java.io.IOException;

/**
 * @author Kayler
 * Action invoked when New->New SQF File is called from main menu
 * Created on 04/05/2016.
 */
public class Action_NewSQFFile extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		Dialog_NewSQFFile.showNewInstance(e, new CreateNewSQFFile(e.getProject()));
	}

	@Override
	public void update(AnActionEvent e) {
		boolean enable = e.getProject() == null;
		VirtualFile directory = e.getData(CommonDataKeys.VIRTUAL_FILE);
		enable = enable || directory != null;

		Module module = e.getData(DataKeys.MODULE);
		enable = enable || PluginUtil.moduleIsArmaType(module);
		if(enable){
			try {
				ArmaProjectDataManager.getInstance().getDataForModule(module).getRootMissionDirectory();
			} catch (DescriptionExtNotDefinedException e1) {
				enable = false;
			}
		}
		e.getPresentation().setVisible(enable);
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
						directory.createChildData(null, fileName);
					} catch (IOException e) {
						e.printStackTrace(System.out);
						String title = Plugin.resources.getString("plugin.message.file_creation_error.title");
						String message = String.format(Plugin.resources.getString("plugin.message.file_creation_error"), e.getMessage());
						SimpleMessageDialog.newDialog(title, message).show();
					}
				}
			});
		}
	}
}
