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
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation.SQFFunctionItemPresentation;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation.SQFVariableItemPresentation;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFLocalVarInStringsReference;
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
		ArrayList<SQFVariable> variables = PsiUtil.findDescendantElementsOfInstance(this.getContainingFile(), SQFVariable.class, null, me.getVarName());
		ArrayList<SQFVariable> refVars = new ArrayList<>();
		for (SQFVariable variable : variables) {
			if (myDeclarationScope == variable.getDeclarationScope()) {
				refVars.add(variable);
			}
		}
		final String varNameAsQuote = "\"" + me.getVarName() + "\"";

		//get all for loop strings
		ArrayList<SQFString> stringMatches = PsiUtil.findDescendantElementsOfInstance(getContainingFile(), SQFString.class, null, varNameAsQuote);
		ArrayList<SQFString> strings = new ArrayList<>();
		for (SQFString string : stringMatches) {
			if (SQFPsiUtil.getForVarScope(string) == myDeclarationScope) { //compare for-loop scope to declaration scope
				strings.add(string);
			}
		}
		//get all strings inside declaration scope (can differ from for-loop scope)
		stringMatches = PsiUtil.findDescendantElementsOfInstance(myDeclarationScope, SQFString.class, null, varNameAsQuote);
		for (SQFString string : stringMatches) {
			if (!strings.contains(string)) {
				strings.add(string);
			}
		}


		if (refVars.size() <= 0 && strings.size() <= 0) {
			return PsiReference.EMPTY_ARRAY;
		}
		PsiReference varReference = null;
		PsiReference stringReference = null;
		if (refVars.size() > 0) {
			varReference = new SQFVariableReference(me, refVars);
		}
		if (strings.size() > 0) {
			stringReference = new SQFLocalVarInStringsReference(this, strings);
		}

		if (varReference != null && stringReference != null) {
			return new PsiReference[]{varReference, stringReference};
		}
		if (varReference != null) {
			return new PsiReference[]{varReference};
		}

		return new PsiReference[]{stringReference};
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
