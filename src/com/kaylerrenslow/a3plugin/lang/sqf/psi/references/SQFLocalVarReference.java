package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 04/08/2016.
 */
public class SQFLocalVarReference implements PsiReference {
	private final SQFVariable var;
	private final SQFPrivateDeclVar declVar;

	public SQFLocalVarReference(SQFVariable var, SQFPrivateDeclVar declVar) {
		this.var = var;
		this.declVar = declVar;
	}

	@Override
	public PsiElement getElement() {
		return declVar;
	}

	@Override
	public TextRange getRangeInElement() {
		return TextRange.from(1, declVar.getNode().getTextLength() - 2);
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		return var;
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return var.getVarName();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		var.setName(newElementName);
		SQFPrivateDeclVar newElement = SQFPsiUtil.createPrivateDeclVarElement(declVar.getProject(), newElementName);
		declVar.getParent().getNode().replaceChild(declVar.getNode(), newElement.getNode());
		return declVar;
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		if (!(element instanceof SQFVariableNamedElement)) {
			return false;
		}
		SQFVariableNamedElement other = (SQFVariableNamedElement) element;
		SQFScope myScope = SQFPsiUtil.getContainingScope(declVar);
		SQFScope otherScope = ((SQFVariable) element).getDeclarationScope();
		return myScope == otherScope && other.getVarName().equals(getCanonicalText());
		//		if (!(element instanceof SQFVariableNamedElement)) {
		//			return false;
		//		}
		//		SQFVariableNamedElement other = (SQFVariableNamedElement) element;
		//		PsiElement selfResolve = resolve();
		//
		//		boolean referenceTo = other.getVarName().equals(getCanonicalText()) && resolve() != other;
		//
		//		SQFScope myScope = ((SQFVariable) selfResolve).getDeclarationScope();
		//		SQFScope otherScope = ((SQFVariable) element).getDeclarationScope();
		//		referenceTo = referenceTo && myScope == otherScope && other.getContainingFile() == selfResolve.getContainingFile();
		//		return referenceTo;

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
