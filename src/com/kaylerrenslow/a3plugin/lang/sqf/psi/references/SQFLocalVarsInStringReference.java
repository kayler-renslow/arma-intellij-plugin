package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFScope;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFString;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 @author Kayler
 A reference between at least one local variable and a String that contains the variable (differs from {@link SQFLocalVarInStringsReference} such that this resolves to a {@link SQFVariable} where
 the other resovles to a {@link SQFString})
 Created on 04/12/2016. */
public class SQFLocalVarsInStringReference implements PsiPolyVariantReference {
	private final List<SQFVariable> targetVars;
	private final SQFString string;
	private final SQFScope targetVarScope;
	private ResolveResult[] resolveResult;

	public SQFLocalVarsInStringReference(List<SQFVariable> targetVars, SQFScope targetVarScope, SQFString string) {
		this.targetVars = targetVars;
		this.string = string;
		this.targetVarScope = targetVarScope;

		resolveResult = PsiElementResolveResult.createResults(targetVars);
	}

	@Override
	public PsiElement getElement() {
		return string;
	}

	@Override
	public TextRange getRangeInElement() {
		return string.getNonQuoteRangeRelativeToElement();
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		return targetVars.get(0);
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return targetVars.get(0).getVarName();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		SQFString newString = SQFPsiUtil.createNewStringLiteral(this.string.getProject(), newElementName);
		this.string.getParent().getNode().replaceChild(string.getNode(), newString.getNode());
		return this.string;
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		if (element instanceof SQFString) {
			return element.getText().equals(this.string.getText());
		}
		if (!(element instanceof SQFVariable)) {
			return false;
		}
		SQFVariable paramVar = (SQFVariable) element;
		PsiReference[] references = paramVar.getReferences();
		for (PsiReference reference : references) {
			if (reference.resolve() == paramVar && paramVar.getDeclarationScope() == targetVarScope) {
				return true;
			}
		}
		return false;
	}

	@NotNull
	@Override
	public Object[] getVariants() {
		return new Object[0];
	}

	@Override
	public String toString() {
		return "SQFLocalVarInStringReference{" +
				"targetVars=" + targetVars +
				", string=" + string +
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
