package com.kaylerrenslow.a3plugin.dialog.actions.newGroup;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.dialog.SimpleMessageDialog;
import com.kaylerrenslow.a3plugin.dialog.newGroup.SQFConfigFunctionInformationHolder;
import com.kaylerrenslow.a3plugin.dialog.newGroup.functionCreation.FunctionCreationDialog;
import com.kaylerrenslow.a3plugin.lang.header.exception.DescriptionExtNotDefinedException;
import com.kaylerrenslow.a3plugin.lang.header.exception.GenericConfigException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.project.ArmaProjectDataManager;
import com.kaylerrenslow.a3plugin.util.PluginUtil;

import java.awt.*;
import java.io.IOException;

/**
 * @author Kayler
 * Action invoked when New->New SQF Function is called from main menu
 * Created on 04/05/2016.
 */
public class Action_NewSQFFunction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent e) {
		Module module = e.getData(DataKeys.MODULE);
		VirtualFile directory = e.getData(CommonDataKeys.VIRTUAL_FILE);
		PsiDirectory missionDirectoryRoot;
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
		if(psiDirectory == null || missionDirectoryRoot.equals(directory)){
			functionDirectoryPath = null;
		}else{
			PsiDirectory cur = psiDirectory;
			functionDirectoryPath = cur.getName();
			while(cur.getParentDirectory() != null){
				if(missionDirectoryRoot.getVirtualFile().equals(cur.getParent().getVirtualFile())){
					break;
				}
				cur = cur.getParentDirectory();
				functionDirectoryPath = cur.getName() + "\\" + functionDirectoryPath;
			}
		}
		Component contextComponent = DataKeys.CONTEXT_COMPONENT.getData(e.getDataContext());
		FunctionCreationDialog dialog = FunctionCreationDialog.showNewInstance(contextComponent, module, functionDirectoryPath);
		if(dialog.dialogFinished()){
			createFunction(dialog.getNewFunctionDefinition(), module.getProject(), directory);
		}
	}

	private void createFunction(SQFConfigFunctionInformationHolder data, Project project, VirtualFile directoryFile){
		WriteCommandAction.runWriteCommandAction(project, new Runnable() {
			@Override
			public void run() {
				String fileName = SQFStatic.getConfigFunctionFileName(data.functionClassName);

				VirtualFile created;

				try {
					created = directoryFile.createChildData(null, fileName);
					FileEditorManager.getInstance(project).openFile(created, true);
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

}
