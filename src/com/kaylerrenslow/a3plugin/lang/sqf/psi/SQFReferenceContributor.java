package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.psiUtil.PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by Kayler on 03/20/2016.
 */
public class SQFReferenceContributor extends PsiReferenceContributor{
	@Override
	public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
		registrar.registerReferenceProvider(PlatformPatterns.psiElement(SQFFile.class), new PsiReferenceProvider(){
			@NotNull
			@Override
			public PsiReference[] getReferencesByElement(@NotNull PsiElement file, @NotNull ProcessingContext context) {
				System.out.println(file.getContainingFile().getName());
				ArrayList<ASTNode> vars = PsiUtil.findElements((PsiFile)file, SQFTypes.VARIABLE, null);
				ArrayList<PsiReference> references = new ArrayList<>();
				for(ASTNode node : vars){
					if(PsiUtil.isOfElementType(node, SQFTypes.GLOBAL_VAR)){
						references.add(new SQFReference(node.getPsi(), TextRange.allOf(node.getText())));
					}
				}
				PsiReference[] arr = new PsiReference[0];
				return references.toArray(arr);
			}
		});
	}
}
