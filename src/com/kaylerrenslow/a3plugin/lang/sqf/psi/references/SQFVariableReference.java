package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
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
//		if (!(element instanceof SQFVariable)) {
//			return false;
//		}
//		PsiElement selfResolve = resolve();
//		if(selfResolve == element || selfResolve == null){
//			return false;
//		}
//		SQFVariable other = (SQFVariable) element;
//
//		boolean referenceTo = other.getVarName().equals(target.getVarName());
//
//		if (!var.isGlobalVariable()) {
//			SQFScope myScope = ((SQFVariable) selfResolve).getDeclarationScope();
//			SQFScope otherScope = ((SQFVariable) element).getDeclarationScope();
//			referenceTo = referenceTo && myScope == otherScope && other.getContainingFile() == selfResolve.getContainingFile();
//			return referenceTo;
//		}
//		return referenceTo;
		return false;
	}

	@NotNull
	@Override
	public Object[] getVariants() {
		return new Object[0];
	}

	@Override
	public String toString() {
		return "SQFVariableReference{" +
				"target=" + target +
				", var=" + var +
				'}';
	}

	@Override
	public boolean isSoft() {
		return false;
	}
}
