package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTokenType;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFVariableReference;
import org.jetbrains.annotations.NotNull;

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
		if (!(element instanceof SQFVariable)){
			return new PsiReference[0]; //can't be referenced
		}
		SQFVariable var = (SQFVariable) element;

		if(var.getVariableType() == SQFTypes.GLOBAL_VAR){
			List<SQFVariable> vars = SQFPsiUtil.findGlobalVariables(element.getProject(), var);
			PsiReference[] references = new PsiReference[vars.size()];
			for (int i = 0; i < vars.size(); i++){
				references[i] = new SQFVariableReference(var, vars.get(i));
			}
			return references;
		}
		return new PsiReference[0];
	}

}
