package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.impl.SQFRefactorableReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 03/20/2016.
 */
public class SQFRefactoringSupportProvider extends RefactoringSupportProvider{

	@Override
	public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, @Nullable PsiElement context) {
		return element instanceof SQFRefactorableReference || element instanceof PsiNamedElement;
	}
}
