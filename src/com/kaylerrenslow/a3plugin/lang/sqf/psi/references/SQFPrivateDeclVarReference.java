package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * PsiReference for SQF language's PrivateDeclVar PsiElements
 * Created on 03/22/2016.
 */
public class SQFPrivateDeclVarReference implements SQFRefactorableReference{

	private final SQFVariableNamedElement target;
	private final SQFPrivateDeclVar myDeclVar;
	private final TextRange range;

	public SQFPrivateDeclVarReference(SQFVariableNamedElement target, SQFPrivateDeclVar var) {
		this.myDeclVar = var;
		this.range = TextRange.from(0, target.getTextLength());
		this.target = target;
	}

	@Override
	public PsiElement getElement() {
		return myDeclVar;
	}


	@Override
	public TextRange getRangeInElement() {
		return range;
	}


	@Nullable
	@Override
	public PsiElement resolve() {
		return target;
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return myDeclVar.getVarName();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		System.out.println(newElementName);
		SQFPrivateDeclVar newElement = SQFPsiUtil.createPrivateDeclVarElement(this.myDeclVar.getProject(), newElementName);
		this.myDeclVar.getParent().getNode().replaceChild(this.myDeclVar.getNode(), newElement.getNode());
		return newElement;
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		if (!(element instanceof SQFVariableNamedElement)){
			return false;
		}
		SQFVariableNamedElement other = (SQFVariableNamedElement) element;
		SQFScope myScope = SQFPsiUtil.getContainingScope(this.myDeclVar);
		SQFScope otherScope = ((SQFVariable) element).getDeclarationScope();
		return myScope == otherScope && other.getVarName().equals(getCanonicalText());
	}

	@NotNull
	@Override
	public Object[] getVariants() {
		return new Object[0];
	}

	@Override
	public boolean isSoft() {
		return true;
	}

	@Override
	public String toString() {
		return "SQFPrivateDeclVarReference{" +
				"var name=" + myDeclVar.getText() + '}';
	}
}
