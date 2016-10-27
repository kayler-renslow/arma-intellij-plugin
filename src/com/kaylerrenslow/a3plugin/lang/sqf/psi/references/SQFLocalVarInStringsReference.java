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
 A reference between a local variable and at leat one String that contains the variable ("_i" where variable is _i for example). (differs from {@link SQFLocalVarsInStringReference} such that
 this resolves to {@link SQFString} where the other resolves to a {@link SQFVariable}
 Created on 10/26/2016. */
public class SQFLocalVarInStringsReference implements PsiPolyVariantReference {
	private final SQFVariable variable;
	private final List<SQFString> strings;
	private final ResolveResult[] resolveResult;

	public SQFLocalVarInStringsReference(SQFVariable variable, List<SQFString> strings) {
		this.variable = variable;
		this.strings = strings;
		resolveResult = PsiElementResolveResult.createResults(strings);
	}

	@Override
	public PsiElement getElement() {
		return variable;
	}

	@Override
	public TextRange getRangeInElement() {
		return TextRange.from(0, variable.getTextLength());
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		return strings.get(0);
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return strings.get(0).getNonQuoteText();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		SQFVariable newVar = SQFPsiUtil.createVariable(this.variable.getProject(), newElementName);
		this.variable.getParent().getNode().replaceChild(variable.getNode(), newVar.getNode());
		return this.variable;
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		if (element instanceof SQFVariable) {
			SQFVariable paramVar = (SQFVariable) element;
			PsiReference[] references = paramVar.getReferences();
			final SQFScope varDeclScope = variable.getDeclarationScope();
			for (PsiReference reference : references) {
				if (reference.resolve() == paramVar && paramVar.getDeclarationScope() == varDeclScope) {
					return true;
				}
			}
		}
		if (!(element instanceof SQFString)) {
			return false;
		}
		SQFString paramStr = (SQFString) element;
		PsiReference[] references = paramStr.getReferences();
		for (PsiReference reference : references) {
			if (reference.resolve() == paramStr) {
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
		return "SQFLocalVarInStringsReference{" +
				"variable=" + variable +
				", strings=" + strings +
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
