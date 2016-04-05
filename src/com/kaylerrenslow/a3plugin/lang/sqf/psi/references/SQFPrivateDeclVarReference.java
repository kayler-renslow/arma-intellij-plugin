package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.lang.ASTNode;
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

	private PsiElement myElement, target;
	private TextRange range;

	public SQFPrivateDeclVarReference(SQFVariableNamedElement target, SQFPrivateDeclVar var) {
		this.myElement = var;
		this.range = TextRange.from(0, target.getTextLength());
		this.target = target;
	}

	@Override
	public PsiElement getElement() {
		return myElement;
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
		return myElement.getText();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		ASTNode parent = myElement.getParent().getNode();
		PsiElement newElement = SQFPsiUtil.createPrivateDeclVarElement(myElement.getProject(), newElementName);
		parent.replaceChild(myElement.getNode(), newElement.getNode());
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
		SQFScope myScope = SQFPsiUtil.getContainingScope(this.myElement);
		SQFScope otherScope = ((SQFVariable) element).getDeclarationScope();
		return myScope == otherScope && other.getName().equals(getCanonicalText());
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
				"var name=" + myElement.getText() + '}';
	}
}
