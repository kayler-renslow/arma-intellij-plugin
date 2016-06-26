package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFScope;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation.SQFFunctionItemPresentation;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation.SQFVariableItemPresentation;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFVariableReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 @author Kayler
 PsiElement mixin for SQF grammar file. This mixin is meant for SQFVariables
 Created on 03/19/2016. */
public abstract class SQFVariableNamedElementMixin extends ASTWrapperPsiElement implements SQFVariableNamedElement, SQFVariable {
	private final IElementType myVariableElementType;

	public SQFVariableNamedElementMixin(@NotNull ASTNode node) {
		super(node);
		this.myVariableElementType = this.getNode().getFirstChildNode().getElementType();
	}

	@Override
	public IElementType getVariableType() {
		return this.myVariableElementType;
	}

	@Override
	public boolean isGlobalVariable() {
		return this.myVariableElementType == SQFTypes.GLOBAL_VAR;
	}

	@Override
	public boolean isLangVar() {
		return this.myVariableElementType == SQFTypes.LANG_VAR;
	}

	@Override
	public ItemPresentation getPresentation() {
		if (this.isGlobalVariable()) {
			if (SQFStatic.followsSQFFunctionNameRules(this.getVarName())) {
				return new SQFFunctionItemPresentation(this.getVarName(), this.getContainingFile());
			}
		}
		return new SQFVariableItemPresentation(this);
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		SQFVariable me = this;
		SQFScope myDeclarationScope = me.getDeclarationScope();

		//need to search entire file because of for spec case (for[{private _i = 0},{},{}] do{/*_i can be referenced here, but from here _i won't be seen*/})
		ArrayList<ASTNode> nodes = PsiUtil.findDescendantElements(this.getContainingFile(), SQFTypes.VARIABLE, null, me.getVarName());
		SQFVariable other;
		ArrayList<SQFVariable> refVars = new ArrayList<>();
		for (int i = 0; i < nodes.size(); i++) {
			other = ((SQFVariable) nodes.get(i).getPsi());
			if (myDeclarationScope == other.getDeclarationScope()) {
				refVars.add(other);
			}
		}
		if (refVars.size() > 0) {
			return new PsiReference[]{new SQFVariableReference(me, refVars)};
		}
		return PsiReference.EMPTY_ARRAY;
	}

	@Override
	public PsiReference getReference() {
		PsiReference[] references = getReferences();
		if (references == PsiReference.EMPTY_ARRAY) {
			return null;
		}
		return references[0];
	}

	@Override
	public String toString() {
		return "SQFVariableNamedElementMixin{" + this.getName() + "}";
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
		if (myVariableElementType == SQFTypes.LANG_VAR) {
			throw new IncorrectOperationException("This variable can not be renamed.");
		}
		SQFVariable newVar = SQFPsiUtil.createVariable(this.getProject(), name);
		this.getParent().getNode().replaceChild(this.getNode(), newVar.getNode());
		return newVar;
	}

	@Override
	public String getVarName() {
		return this.getName();
	}
}
