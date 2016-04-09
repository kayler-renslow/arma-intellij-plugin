package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFScope;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * This type of reference is used between two variables. Both variables must have the same element type.
 * Created on 04/08/2016.
 */
public class SQFVariableReference implements PsiReference {
	private final SQFVariable target, var;

	public SQFVariableReference(SQFVariable var, SQFVariable target) {
		this.var = var;
		this.target = target;
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
		return target;
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return target.getVarName();
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
		if (!(element instanceof SQFVariable)) {
			return false;
		}
		SQFVariable other = (SQFVariable) element;
		PsiElement selfResolve = resolve();

		boolean referenceTo = other.getVarName().equals(target.getVarName());

		IElementType myVariableElementType = var.getVariableType();
		if (myVariableElementType != SQFTypes.GLOBAL_VAR) {
			SQFScope myScope = ((SQFVariable) selfResolve).getDeclarationScope();
			SQFScope otherScope = ((SQFVariable) element).getDeclarationScope();
			referenceTo = referenceTo && myScope == otherScope && other.getContainingFile() == selfResolve.getContainingFile();
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
	public boolean isSoft() {
		return false;
	}
}
