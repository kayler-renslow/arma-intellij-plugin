package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.kaylerrenslow.a3plugin.lang.header.exception.GenericConfigException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderConfigFunction;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFCommandElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * RefactoringSupportProvider extension point for SQF language. This decides what can be refactored *IN-LINE* and what can't
 * Created on 03/20/2016.
 */
public class SQFRefactoringSupportProvider extends RefactoringSupportProvider{

	@Override
	public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, @Nullable PsiElement context) {
		if(!(element instanceof PsiNamedElement)){
			return false;
		}
		if(element instanceof SQFCommandElement){
			return false;
		}
		if(element instanceof SQFVariable){
			SQFVariable variable = (SQFVariable)element;
			if(SQFStatic.followsSQFFunctionNameRules(variable.getVarName())){
				try{
					HeaderConfigFunction function = HeaderPsiUtil.getFunctionFromCfgFunctions(element.getContainingFile(),variable.getVarName());
					return function != null;
				}catch (GenericConfigException e){
					return false;
				}
			}
		}
		return true;
	}
}
