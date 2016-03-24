package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.helpers.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.impl.references.SQFVariableAsStringReference;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kayler on 03/20/2016.
 */
public class SQFReferenceContributor extends PsiReferenceContributor{
	@Override
	public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
		registrar.registerReferenceProvider(PlatformPatterns.psiElement(SQFVariable.class), new PsiReferenceProvider(){
			@NotNull
			@Override
			public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
				if (!(element instanceof SQFVariableNamedElement)){
					return new PsiReference[0];
				}

				SQFVariableNamedElement var = (SQFVariableNamedElement) element;

				if (var.getVariableType() == SQFTypes.LOCAL_VAR){
					SQFScope varScope = SQFPsiUtil.getCurrentScopeForVariable((SQFVariable) var);
					ArrayList<ASTNode> nodes = PsiUtil.findChildElements(SQFPsiUtil.getCurrentScope(element), SQFTypes.VARIABLE_AS_STRING, null, "\"" + var.getText() + "\"");
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
						references[i] = new SQFVariableAsStringReference(var, (SQFVariableAsString)ele);
					}
					return references;
				}

				List<SQFVariable> vars = SQFPsiUtil.findGlobalVariables(element.getProject(), var.getName());
				PsiReference[] references = new PsiReference[vars.size()];
				for (int i = 0; i < vars.size(); i++){
					references[i] = vars.get(i).getReference();
				}
				return references;
			}
		});
	}
}
