package com.kaylerrenslow.a3plugin.lang.sqf.contributors;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.providers.SQFReferenceProvider;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * Registers, to Intellij, which references provider implementations are for SQF language
 * Created on 03/20/2016.
 */
public class SQFReferenceContributor extends PsiReferenceContributor{
	@Override
	public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
		registrar.registerReferenceProvider(PlatformPatterns.psiElement(SQFVariable.class), new SQFReferenceProvider());
	}
}
