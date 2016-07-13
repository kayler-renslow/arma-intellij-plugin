package com.kaylerrenslow.a3plugin.lang.sqf.contributors;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.BaseRefactoringProcessor;
import com.intellij.refactoring.rename.RenameHandler;
import com.intellij.refactoring.rename.RenameProcessor;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.dialog.SimpleMessageDialog;
import com.kaylerrenslow.a3plugin.dialog.newGroup.SQFConfigFunctionInformationHolder;
import com.kaylerrenslow.a3plugin.dialog.newGroup.functionRename.FunctionRenameDialog;
import com.kaylerrenslow.a3plugin.lang.header.exception.DescriptionExtNotDefinedException;
import com.kaylerrenslow.a3plugin.lang.header.exception.GenericConfigException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderConfigFunction;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFFile;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFCommandElement;
import com.kaylerrenslow.a3plugin.project.ArmaProjectDataManager;
import com.kaylerrenslow.a3plugin.util.FilePath;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author Kayler
 *         Provides custom renaming for SQF config functions.
 *         Created on 04/27/2016.
 */
public class SQFRenameHandler implements RenameHandler {

	@Override
	public boolean isAvailableOnDataContext(DataContext dataContext) {
		if (!(dataContext.getData(CommonDataKeys.PSI_FILE.getName()) instanceof SQFFile)) {
			return false;
		}
		Module module = DataKeys.MODULE.getData(dataContext);
		if (module == null) {
			return false;
		}
		PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
		if(psiElement instanceof SQFCommandElement){
			return true;
		}
		if (psiElement instanceof SQFVariable) {
			SQFVariable var = (SQFVariable) psiElement;
			if (SQFStatic.followsSQFFunctionNameRules(var.getVarName())) {
				try {
					HeaderPsiUtil.getFunctionFromCfgFunctions(module, var.getVarName());
				} catch (GenericConfigException e) {
					return false;
				}
				return true;
			}
		}
		return false; //use default rename method
	}

	@Override
	public boolean isRenaming(DataContext dataContext) {
		return isAvailableOnDataContext(dataContext);
	}

	@Override
	public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
		PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
		if (!(psiElement instanceof SQFVariable)) {
			return;
		}
		Module module = DataKeys.MODULE.getData(dataContext);
		if (module == null) {
			return;
		}

		SQFVariable variable = (SQFVariable) psiElement;
		Component contextComponent = DataKeys.CONTEXT_COMPONENT.getData(dataContext);
		HeaderConfigFunction function;
		try {
			function = HeaderPsiUtil.getFunctionFromCfgFunctions(module, variable.getVarName());
		} catch (GenericConfigException e) {
			SimpleMessageDialog.showNewDialog("Error", e.getMessage());
			return;
		}

		FunctionRenameDialog renameDialog = FunctionRenameDialog.showNewInstance(contextComponent, module, function);
		if(renameDialog.dialogFinished()){
			rename(renameDialog.getNewFunctionDefinition(), renameDialog.getRenameRootTagValue(), variable, function, module);
		}
	}

	private void rename(SQFConfigFunctionInformationHolder neww, boolean renameRootTag, SQFVariable variable, HeaderConfigFunction old, Module module) {
		WriteCommandAction.runWriteCommandAction(module.getProject(), new Runnable() {
			@Override
			public void run() {
				boolean renameFunction = false;
				String newFunctionName = SQFStatic.getFullFunctionName(neww.functionTagName, neww.functionClassName);
				HeaderConfigFunction function;
				try {
					function = HeaderPsiUtil.getFunctionFromCfgFunctions(module, old.getCallableName());
				} catch (GenericConfigException e) {
					SimpleMessageDialog.showNewDialog("Error", e.getMessage());
					return;
				}

				if (!old.getTagName().equals(neww.functionTagName)) {
					if (renameRootTag) {
						function.getClassWithTag().setAttribute("tag", "\"" + neww.functionTagName + "\"");
						java.util.List<SQFVariable> vars = SQFPsiUtil.findConfigFunctionVariablesWithTag(module, old.getTagName());
						//we don't need to update the references for each of the variables because ALL cases where the tag matches is returned

						SQFStatic.SQFFunctionTagAndName tagAndName;
						for (SQFVariable v : vars) {
							tagAndName = SQFStatic.getFunctionTagAndName(v.getVarName());
							v.setName(SQFStatic.getFullFunctionName(neww.functionTagName, tagAndName.functionClassName));
						}

					} else {
						try {
							HeaderPsiUtil.insertNewFunctionIntoCfgFunctions(neww); //guaranteed not to exist because this check was done inside the dialog
						} catch (GenericConfigException e) {
							SimpleMessageDialog.showNewDialog("Error", e.getMessage());
						}
						function.getClassDeclaration().removeFromTree();
						renameFunction = true;
					}
				}
				if (!old.getFunctionFileName().equals(neww.functionFileName)) {
					PsiDirectory rootMissionDirectory;
					PsiFile sqfFile = null;
					try {
						rootMissionDirectory = ArmaProjectDataManager.getInstance().getDataForModule(module).getRootMissionDirectory();
						sqfFile = PluginUtil.findFileByPath(FilePath.getFilePathFromString(function.getFullRelativePath(), '/'), rootMissionDirectory, module.getProject());
					} catch (DescriptionExtNotDefinedException e) {
						SimpleMessageDialog.showNewDialog("Error", e.getMessage());
					}
					if (sqfFile == null) {
						SimpleMessageDialog.showNewDialog("Error", "The SQF file doesn't exist for function: " + function.getCallableName());
					} else {
						try {
							sqfFile.setName(neww.functionFileName);
						} catch (IncorrectOperationException e) {
							SimpleMessageDialog.showNewDialog("Error", e.getMessage());
						}
					}
					function.getClassDeclaration().setAttribute("file", "\""+neww.functionFileName+"\"");

				}
				if (!old.getFunctionClassName().equals(neww.functionClassName)) {
					function.getClassDeclaration().getClassStub().setName(neww.functionClassName);
					renameFunction = true;
				}

				if (renameFunction) {
					invokeRefactoring(createRenameProcessor(variable.getProject(), variable, newFunctionName));
				}

				//				CreateTemplateInPackageaction
				//				FileTemplateUtil
			}
		});

	}

	private RenameProcessor createRenameProcessor(Project project, PsiElement myPsiElement, String newName) {
		return new RenameProcessor(project, myPsiElement, newName, false, false);
	}

	private void invokeRefactoring(BaseRefactoringProcessor processor) {
		processor.setPrepareSuccessfulSwingThreadCallback(new Runnable() {
			@Override
			public void run() {
				//						System.out.println("SQFRenameHandler.run");
			}
		});
		processor.setPreviewUsages(false);
		processor.run();
	}

	@Override
	public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
		System.out.println("SQFRenameHandler.invoke");
	}
}
