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
 * Created by Kayler on 03/23/2016.
 */
public class SQFReferenceProvider extends PsiReferenceProvider{


	@NotNull
	@Override
	public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
		if (!(element instanceof SQFVariableNamedElement)){
			return new PsiReference[0];
		}

		SQFVariableNamedElement var = (SQFVariableNamedElement) element;

		if (var.getVariableType() == SQFTypes.LOCAL_VAR){
			return getPsiReferencesForLocalVar(element, var);
		}

		List<SQFVariable> vars = SQFPsiUtil.findGlobalVariables(element.getProject(), var.getName());
		PsiReference[] references = new PsiReference[vars.size()];
		for (int i = 0; i < vars.size(); i++){
			references[i] = vars.get(i).getReference();
		}
		return references;
	}

	@NotNull
	private PsiReference[] getPsiReferencesForLocalVar(@NotNull PsiElement element, SQFVariableNamedElement var) {
		SQFScope varScope = SQFPsiUtil.getCurrentScopeForVariable((SQFVariable) var);
		ArrayList<ASTNode> nodes = PsiUtil.findDescendantElements(SQFPsiUtil.getCurrentScope(element), SQFTypes.PRIVATE_DECL_VAR, null, "\"" + var.getText() + "\"");
		ArrayList<PsiElement> referencesList = new ArrayList<>();
		for (int i = 0; i < nodes.size(); i++){
			if(varScope == SQFPsiUtil.getCurrentScope(nodes.get(i).getPsi())){
				referencesList.add(nodes.get(i).getPsi());
			}
		}

		PsiReference[] references = new PsiReference[referencesList.size()];
		PsiElement ele;
		for (int i = 0; i < referencesList.size(); i++){
			ele = referencesList.get(i);
			references[i] = new SQFPrivateDeclVarReference(var, (SQFPrivateDeclVar)ele);
		}
		return references;
	}
}
