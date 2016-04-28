package com.kaylerrenslow.a3plugin.lang.sqf.contributors;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.BaseRefactoringProcessor;
import com.intellij.refactoring.rename.RenameHandler;
import com.intellij.refactoring.rename.RenameProcessor;
import com.kaylerrenslow.a3plugin.dialog.Dialog_ConfigFunctionRename;
import com.kaylerrenslow.a3plugin.dialog.SQFConfigFunctionInformationHolder;
import com.kaylerrenslow.a3plugin.dialog.SimpleMessageDialog;
import com.kaylerrenslow.a3plugin.dialog.actions.SimpleGuiAction;
import com.kaylerrenslow.a3plugin.lang.header.exception.*;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderFileEntry;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderConfigFunction;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFFile;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.util.Attribute;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Kayler on 04/27/2016.
 */
public class SQFRenameHandler implements RenameHandler {
	@Override
	public boolean isAvailableOnDataContext(DataContext dataContext) {
		return dataContext.getData(CommonDataKeys.PSI_FILE.getName()) instanceof SQFFile;
	}

	@Override
	public boolean isRenaming(DataContext dataContext) {
		return isAvailableOnDataContext(dataContext);
	}

	@Override
	public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
		PsiElement element = (PsiElement) dataContext.getData(CommonDataKeys.PSI_ELEMENT.getName());
		if (!(element instanceof SQFVariable)) {
			return;
		}
		SQFVariable variable = (SQFVariable) element;
		if (!variable.followsSQFFunctionNameRules()) {
			return;
		}
		Module module = DataKeys.MODULE.getData(dataContext);
		Component contextComponent = DataKeys.CONTEXT_COMPONENT.getData(dataContext);
		Dialog_ConfigFunctionRename.showNewInstance(contextComponent, module, variable.getVarName(), new SimpleGuiAction<Pair<SQFConfigFunctionInformationHolder, SQFConfigFunctionInformationHolder>>() {
			@Override
			public void actionPerformed(Pair<SQFConfigFunctionInformationHolder, SQFConfigFunctionInformationHolder> actionData) {
				rename(actionData, variable);
			}
		});
	}

	private void rename(Pair<SQFConfigFunctionInformationHolder, SQFConfigFunctionInformationHolder> actionData, SQFVariable variable) {
		WriteCommandAction.runWriteCommandAction(actionData.first.module.getProject(), new Runnable() {
			@Override
			public void run() {
				SQFConfigFunctionInformationHolder old = actionData.first;
				SQFConfigFunctionInformationHolder neww = actionData.second;

				String newFunctionName = SQFStatic.getFullFunctionName(neww.functionTagName, neww.functionClassName);
				HeaderConfigFunction function;
				try {
					function = HeaderPsiUtil.getFunctionFromCfgFunctions(old.module, SQFStatic.getFullFunctionName(old.functionTagName, old.functionClassName));
				} catch (GenericConfigException e) {
					SimpleMessageDialog.newDialog("Error", e.getMessage()).show();
					return;
				}
				if(!old.functionTagName.equals(neww.functionTagName) || true){
					function.getClassDeclaration().getClassStub().setName(neww.functionClassName);
					function.getClassWithTag().setAttribute("tag", "\"" + neww.functionTagName + "\"");
				}
				if(!old.functionClassName.equals(neww.functionClassName)){
					invokeRefactoring(createRenameProcessor(variable.getProject(), variable, newFunctionName));
				}


				//				variable.setName(SQFStatic.getFullFunctionName(neww.functionTagName, neww.functionClassName));
				//		List<HeaderFileEntry> fileEntries = function.getClassWithTag().getClassContent().getFileEntries().getFileEntryList();
			}
		});

	}

	private RenameProcessor createRenameProcessor(Project project, PsiElement myPsiElement, String newName) {
		return new RenameProcessor(project, myPsiElement, newName, false, false);
	}

	private void invokeRefactoring(BaseRefactoringProcessor processor) {
		//		processor.setPrepareSuccessfulSwingThreadCallback(prepareSuccessfulCallback);
		processor.setPreviewUsages(false);
		processor.run();
	}

	@Override
	public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
		System.out.println("SQFRenameHandler.invoke");
	}
}
