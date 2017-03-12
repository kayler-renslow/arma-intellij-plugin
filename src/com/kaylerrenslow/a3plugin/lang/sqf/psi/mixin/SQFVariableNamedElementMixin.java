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
import com.kaylerrenslow.a3plugin.lang.sqf.SQFVariableName;
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
 * PsiElement mixin for SQF grammar file. This mixin is meant for SQFVariables
 *
 * @author Kayler
 * @since 03/19/2016
 */
public abstract class SQFVariableNamedElementMixin extends ASTWrapperPsiElement implements SQFVariableNamedElement, SQFVariable {
	private final IElementType myVariableElementType;
	private SQFVariableName varNameCached;

	public SQFVariableNamedElementMixin(@NotNull ASTNode node) {
		super(node);
		this.myVariableElementType = this.getNode().getFirstChildNode().getElementType();
		varNameCached = new SQFVariableName(node.getText());
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
				return new SQFFunctionItemPresentation(this.getVarName().text(), this.getContainingFile());
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
		ArrayList<SQFVariable> variables = PsiUtil.findDescendantElementsOfInstance(this.getContainingFile(), SQFVariable.class, null);
		ArrayList<SQFVariable> refVars = new ArrayList<>();
		for (SQFVariable variable : variables) {
			if (!variable.getVarName().equals(me.getVarName())) {
				continue;
			}
			if (myDeclarationScope == variable.getDeclarationScope()) {
				refVars.add(variable);
			}
		}
		//get all for loop strings
		ArrayList<SQFString> stringMatches = PsiUtil.findDescendantElementsOfInstance(getContainingFile(), SQFString.class, null);
		ArrayList<SQFString> strings = new ArrayList<>();
		for (SQFString string : stringMatches) {
			if (!me.getVarName().equals(string.getNonQuoteText())) {
				continue;
			}
			if (SQFPsiUtil.getForVarScope(string) == myDeclarationScope) { //compare for-loop scope to declaration scope
				strings.add(string);
			}
		}
		//get all strings inside declaration scope (can differ from for-loop scope)
		stringMatches = PsiUtil.findDescendantElementsOfInstance(myDeclarationScope, SQFString.class, null);
		for (SQFString string : stringMatches) {
			if (!me.getVarName().equals(string.getNonQuoteText())) {
				continue;
			}
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
		return "SQFVariableNamedElementMixin{" + getVarName() + "}";
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier() {
		return this.getNode().getPsi();
	}

	@Override
	public String getName() {
		return getText();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
		if (myVariableElementType == SQFTypes.LANG_VAR) {
			throw new IncorrectOperationException("This variable can not be renamed.");
		}
		SQFVariable newVar = SQFPsiUtil.createVariable(this.getProject(), name);
		this.getParent().getNode().replaceChild(this.getNode(), newVar.getNode());
		this.varNameCached = new SQFVariableName(name);
		return newVar;
	}

	@NotNull
	@Override
	public SQFVariableName getVarName() {
		if (getText().equals(varNameCached.textOriginal())) {
			return varNameCached;
		}
		varNameCached = new SQFVariableName(getText());
		return varNameCached;
	}
}
