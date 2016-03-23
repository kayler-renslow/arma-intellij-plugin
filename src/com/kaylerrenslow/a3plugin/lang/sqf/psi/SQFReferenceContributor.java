package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.helpers.SQFUtil;
import org.jetbrains.annotations.NotNull;

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
				if(!(element instanceof SQFVariableNamedElement)){
					return new PsiReference[0];
				}
				SQFVariableNamedElement var = (SQFVariableNamedElement) element;
				List<SQFVariable> vars = SQFUtil.findGlobalVariables(element.getProject(), var.getName());
				PsiReference[] references = new PsiReference[vars.size()];
				for(int i = 0; i < vars.size(); i++){
					references[i] = vars.get(i).getReference();
				}
				return references;
			}
		});
	}
}
