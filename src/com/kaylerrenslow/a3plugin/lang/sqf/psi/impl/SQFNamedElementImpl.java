package com.kaylerrenslow.a3plugin.lang.sqf.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFNamedElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFReference;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.helpers.SQFUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 03/19/2016.
 */
public class SQFNamedElementImpl extends ASTWrapperPsiElement implements SQFNamedElement{
	public SQFNamedElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		PsiReference[] references = ReferenceProvidersRegistry.getReferencesFromProviders(this);
		return ArrayUtil.prepend(getReference(), references);
	}


	@Override
	public PsiReference getReference() {
		return new SQFReference(this);
	}

	@Override
	public String toString() {
		return this.getName();
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
		me.getTreeParent().replaceChild(me, var.getNode());
		return var;
	}

}
