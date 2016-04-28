package com.kaylerrenslow.a3plugin.dialog.actions.newGroup;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.indexing.FileBasedIndex;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.dialog.Dialog_NewSQFFunction;
import com.kaylerrenslow.a3plugin.dialog.SQFConfigFunctionInformationHolder;
import com.kaylerrenslow.a3plugin.dialog.SimpleMessageDialog;
import com.kaylerrenslow.a3plugin.dialog.actions.SimpleGuiAction;
import com.kaylerrenslow.a3plugin.lang.header.exception.DescriptionExtNotDefinedException;
import com.kaylerrenslow.a3plugin.lang.header.exception.GenericConfigException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.project.ArmaProjectDataManager;

import javax.swing.*;
import java.io.IOException;

/**
 * * @author Kayler
 * Action invoked when New->New SQF Function is called from main menu
 * Created on 04/05/2016.
 */
public class Action_NewSQFFunction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent e) {
		Module module = e.getData(DataKeys.MODULE);
		VirtualFile directory = e.getData(CommonDataKeys.VIRTUAL_FILE);
		VirtualFile missionDirectoryRoot;
		try {
			missionDirectoryRoot = ArmaProjectDataManager.getInstance().getDataForModule(module).getRootMissionDirectory();
		} catch (DescriptionExtNotDefinedException e1) {
			return;
		}
		if(!directory.isDirectory()){
			directory = directory.getParent();
		}
		PsiDirectory psiDirectory = PsiManager.getInstance(module.getProject()).findDirectory(directory);

		String functionDirectoryPath;
		if(psiDirectory == null || missionDirectoryRoot == null){
			functionDirectoryPath = null;
		}else{
			PsiDirectory cur = psiDirectory;
			functionDirectoryPath = cur.getName();
			while(cur.getParentDirectory() != null){
				cur = cur.getParentDirectory();
				if(missionDirectoryRoot.equals(cur.getVirtualFile())){
					break;
				}
				functionDirectoryPath = cur.getName() + "\\" + functionDirectoryPath;
			}
		}

		Dialog_NewSQFFunction.showNewInstance(e, module, functionDirectoryPath, new CreateNewSQFFunction(e.getProject(), directory));
	}

	@Override
	public void update(AnActionEvent e) {
		boolean disable = false;
		if(e.getProject() == null){
			disable = true;
		}
		VirtualFile directory = e.getData(CommonDataKeys.VIRTUAL_FILE);
		if(directory == null){
			disable = true;
		}
		Module module = e.getData(DataKeys.MODULE);
		if(module == null){
			disable = true;
		}
		if(!disable){
			try {
				ArmaProjectDataManager.getInstance().getDataForModule(module).getRootMissionDirectory();
			} catch (DescriptionExtNotDefinedException e1) {
				disable = true;
			}
		}
		e.getPresentation().setVisible(!disable);
	}

	private class CreateNewSQFFunction implements SimpleGuiAction<SQFConfigFunctionInformationHolder> {

		private final Project project;
		private final VirtualFile directoryFile;

		CreateNewSQFFunction(Project project, VirtualFile directoryFile) {
			this.project = project;
			this.directoryFile = directoryFile;
		}

		@Override
		public void actionPerformed(SQFConfigFunctionInformationHolder data) {
			WriteCommandAction.runWriteCommandAction(project, new Runnable() {
				@Override
				public void run() {
					String fileName = SQFStatic.getConfigFunctionFileName(data.functionClassName);

					VirtualFile created;

					try {
						created = directoryFile.createChildData(null, fileName);
					} catch (IOException e) {
						e.printStackTrace(System.out);
						String title = Plugin.resources.getString("plugin.message.file_creation_error.title");
						String message = String.format(Plugin.resources.getString("plugin.message.file_creation_error"), e.getMessage());
						SimpleMessageDialog.newDialog(title, message).show();
						return;
					}

					try{
						HeaderPsiUtil.insertNewFunctionIntoCfgFunctions(data);
					}catch(GenericConfigException e){
						e.printStackTrace(System.out);
						String message = String.format(Plugin.resources.getString("plugin.message.function_creation_error"), e.getMessage());
						String title = Plugin.resources.getString("plugin.message.function_creation_error.title");
						SimpleMessageDialog.newDialog(title, message).show();
						return;
					}

					PsiFile psiFile = PsiManager.getInstance(data.module.getProject()).findFile(created);
					if(psiFile == null){
						throw new IllegalStateException("the file should exist");
					}

					
					//set view to new sqf file
				}
			});
		}
	}
}
