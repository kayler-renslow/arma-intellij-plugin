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
 * @author Kayler
 * This type of reference is used between a local variable (e.g. _var) and a private declaration (private ["_var"])
 * Created on 04/08/2016.
 */
public class SQFLocalVarDeclReference implements PsiReference {
	private final SQFVariable targetVar;
	private final SQFPrivateDeclVar declVar;

	public SQFLocalVarDeclReference(SQFVariable targetVar, SQFPrivateDeclVar declVar) {
		this.targetVar = targetVar;
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
		return targetVar;
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return targetVar.getVarName();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
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
	}

	@NotNull
	@Override
	public Object[] getVariants() {
		return new Object[0];
	}

	@Override
	public String toString() {
		return "SQFLocalVarDeclReference{" +
				"targetVar=" + targetVar +
				", declVar=" + declVar +
				'}';
	}

	@Override
	public boolean isSoft() {
		return false;
	}
}
