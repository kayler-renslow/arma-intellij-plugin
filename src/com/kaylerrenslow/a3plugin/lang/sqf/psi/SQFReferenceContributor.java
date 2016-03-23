package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
				return new PsiReference[0];
			}
		});
	}
}
