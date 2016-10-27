package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFScope;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFString;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 @author Kayler
 This type of reference is used between two variables. Both variables must have the same element type.
 Created on 04/08/2016. */
public class SQFVariableReference implements PsiPolyVariantReference {
	private final SQFVariable var;
	private final SQFVariable[] targets;
	private ResolveResult[] resolveResult;

	public SQFVariableReference(SQFVariable var, List<SQFVariable> targets) {
		this.var = var;
		this.targets = targets.toArray(new SQFVariable[targets.size()]);
		resolveResult = PsiElementResolveResult.createResults(targets);
	}

	@Override
	public PsiElement getElement() {
		return var;
	}

	@Override
	public TextRange getRangeInElement() {
		return TextRange.allOf(var.getVarName());
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		return targets[0];
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return targets[0].getVarName();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		return var.setName(newElementName);
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		if (element instanceof SQFString) {
			SQFString sqfString = (SQFString) element;
			return sqfString.getNonQuoteText().equals(var.getVarName());
		}
		if (!(element instanceof SQFVariable)) {
			return false;
		}
		SQFVariable other = (SQFVariable) element;

		boolean referenceTo = other.getVarName().equals(targets[0].getVarName());

		if (!var.isGlobalVariable()) {
			SQFScope myScope = var.getDeclarationScope();
			SQFScope otherScope = other.getDeclarationScope();
			referenceTo = referenceTo && myScope == otherScope && other.getContainingFile() == var.getContainingFile();
			return referenceTo;
		}
		return referenceTo;
	}

	@NotNull
	@Override
	public Object[] getVariants() {
		return new Object[0];
	}

	@Override
	public String toString() {
		return "SQFVariableReference{" +
				"target=" + Arrays.toString(targets) +
				", var=" + var +
				'}';
	}

	@Override
	public boolean isSoft() {
		return false;
	}

	@NotNull
	@Override
	public ResolveResult[] multiResolve(boolean incompleteCode) {
		return resolveResult;
	}
}
