package com.kaylerrenslow.a3plugin.dialog.actions.newGroup;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.kaylerrenslow.a3plugin.dialog.Dialog_NewSQFFunction;
import com.kaylerrenslow.a3plugin.dialog.SQFConfigFunctionInformationHolder;
import com.kaylerrenslow.a3plugin.dialog.actions.SimpleGuiAction;
import com.kaylerrenslow.a3plugin.lang.header.exception.GenericConfigException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.impl.HeaderConfigFunction;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.util.PluginUtil;

import java.io.IOException;

/**
 * Created by Kayler on 04/05/2016.
 */
public class Action_NewSQFFunction extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent e) {
		Dialog_NewSQFFunction.showNewInstance(e, new CreateNewSQFFunction(e.getProject(), e));
	}

	@Override
	public void update(AnActionEvent e) {
		if(e.getProject() == null){
			e.getInputEvent().consume();
			return;
		}

	}

	private class CreateNewSQFFunction implements SimpleGuiAction<SQFConfigFunctionInformationHolder> {

		private final Project project;
		private final AnActionEvent anActionEvent;

		CreateNewSQFFunction(Project project, AnActionEvent anActionEvent) {
			this.project = project;
			this.anActionEvent = anActionEvent;
		}

		@Override
		public void actionPerformed(SQFConfigFunctionInformationHolder data) {
			WriteCommandAction.runWriteCommandAction(project, new Runnable() {
				@Override
				public void run() {
					try{
						HeaderPsiUtil.insertNewFunctionIntoCfgFunctions(data);
					}catch(GenericConfigException e){
						e.printStackTrace(System.out);
						return;
					}
					String fileName = SQFStatic.getConfigFunctionFileName(data.functionClassName);
					VirtualFile directory = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE).getParent();/*PluginUtil.findFileInModuleByPath(data.module, PluginUtil.FilePath.createFilePathFromNames(data.functionLocation.split("\\\\")));
					if(directory == null){
						System.err.println("CreateNewSQFFunction.run");
						directory = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE).getParent();
					}*/
					System.out.println(directory.getPath());
					try {
						VirtualFile created = directory.createChildData(null, fileName);
					} catch (IOException e) {
						e.printStackTrace(System.out);
						return;
					}

					//set view to new sqf file
				}
			});
		}
	}
}
