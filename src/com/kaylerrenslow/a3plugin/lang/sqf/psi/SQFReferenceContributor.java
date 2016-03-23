package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.helpers.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.impl.references.SQFPrivateDeclVarReference;
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
					ArrayList<ASTNode> nodes = PsiUtil.findChildElements(SQFPsiUtil.getCurrentScope(element), SQFTypes.PRIVATE_DECL_VAR, null, "\"" + var.getText() + "\"");
//					System.out.println("SQFReferenceContributor.getReferencesByElement " + nodes.size());
					PsiReference[] references = new PsiReference[nodes.size()];
					ASTNode node;
					for (int i = 0; i < nodes.size(); i++){
						node = nodes.get(i);
						references[i] = new SQFPrivateDeclVarReference(var, (SQFPrivateDeclVar)node.getPsi());
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
