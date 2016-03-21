package com.kaylerrenslow.a3plugin.lang.sqf.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFNamedElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.helpers.SQFUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 03/19/2016.
 */
public abstract class SQFNamedElementImpl extends ASTWrapperPsiElement implements SQFNamedElement{
	public SQFNamedElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public String toString() {
		final String className = getClass().getSimpleName();
		return StringUtil.trimEnd(className, "Impl");
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier() {
		return this.getNode().getPsi();
	}

	@Override
	public String getName() {
		return getNode().getText();
	}


	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
		ASTNode me = this.getNode();
		SQFVariable var = SQFUtil.createVariable(this.getProject(), name);
//		me.replaceChild(); //we should be doing all of this in statements, right?
		return null;
	}

}
