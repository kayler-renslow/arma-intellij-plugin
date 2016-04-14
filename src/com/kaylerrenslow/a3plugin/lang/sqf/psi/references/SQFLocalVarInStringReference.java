package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @author Kayler
 * A reference between a local variable and a String that contains the variable
 * Created on 04/12/2016.
 */
public class SQFLocalVarInStringReference implements PsiReference {
	private final SQFVariable targetVar;
	private final PsiElement string;

	public SQFLocalVarInStringReference(SQFVariable targetVar, PsiElement string) {
		this.targetVar = targetVar;
		this.string = string;
	}

	@Override
	public PsiElement getElement() {
		return string;
	}

	@Override
	public TextRange getRangeInElement() {
		return TextRange.from(1, string.getNode().getTextLength() - 2);
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
		if (!(element instanceof SQFVariableNamedElement)) {
			return false;
		}
		SQFVariable other = (SQFVariable) element;
		SQFScope myScope = SQFPsiUtil.getContainingScope(this.string);
		ArrayList<ASTNode> nodes = PsiUtil.findDescendantElements(myScope, SQFTypes.VARIABLE, null, getCanonicalText());
		for(ASTNode node : nodes){
			if(node == other.getNode()){
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
	public boolean isSoft() {
		return false;
	}
}
