package com.kaylerrenslow.a3plugin.lang.sqf.contributors;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.header.exception.GenericConfigException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 04/26/2016.
 */
public class SQFPsiElementProcessor extends RenamePsiElementProcessor {
	@Override
	public boolean canProcessElement(@NotNull PsiElement element) {
		if(element instanceof SQFVariable){
			SQFVariable variable = (SQFVariable)element;
			if(SQFStatic.followsSQFFunctionNameRules(variable.getVarName())){
				try{
					HeaderPsiUtil.getFunctionFromCfgFunctions(element.getContainingFile(),variable.getName());
					return true;
				}catch (GenericConfigException e){

				}
			}
		}
		return false;
	}
//
//	@Override
//	public void renameElement(PsiElement element, String newName, UsageInfo[] usages, @Nullable RefactoringElementListener listener) throws IncorrectOperationException {
//		super.renameElement(element, newName, usages, listener);
//	}
//	//
//	@Override
//	public RenameDialog createRenameDialog(Project project, PsiElement element, PsiElement nameSuggestionContext, Editor editor) {
//		return new ConfigFunctionRenameDialog(project, element, nameSuggestionContext, editor);
//	}
//
//	/**
//	 * Created by Kayler on 04/27/2016.
//	 */
//	static class ConfigFunctionRenameDialog extends RenameDialog{
//
//		public ConfigFunctionRenameDialog(@NotNull Project project, @NotNull PsiElement psiElement, @Nullable PsiElement nameSuggestionContext, Editor editor) {
//			super(project, psiElement, nameSuggestionContext, editor);
//		}
//
//	//	@Override
//	//	protected JComponent createNorthPanel() {
//	//		JComponent north = super.createNorthPanel();
//	//		this.my
//	//		return ;
//	//	}
//	}
}
