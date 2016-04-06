package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPrivateDeclVar;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFPrivateDeclVarReference;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 * PsiReferenceProvider extension point for SQF language. This provides PSIReferences for given PsiElements, if applicable to them
 * Created on 03/23/2016.
 */
public class SQFReferenceProvider extends PsiReferenceProvider{


	@NotNull
	@Override
	public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
		if (!(element instanceof SQFVariableNamedElement)){
			return new PsiReference[0]; //can't be referenced
		}

		SQFVariableNamedElement var = (SQFVariableNamedElement) element;

		if(var.getVariableType() == SQFTypes.GLOBAL_VAR){
			List<SQFVariable> vars = SQFPsiUtil.findGlobalVariables(element.getProject(), var.getName());
			PsiReference[] references = new PsiReference[vars.size()];
			for (int i = 0; i < vars.size(); i++){
				references[i] = vars.get(i).getReference();
			}
			return references;
		}
		return new PsiReference[0];
	}

}
