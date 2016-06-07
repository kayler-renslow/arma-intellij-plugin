package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFScope;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFString;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * A reference between a local variable and a String that contains the variable
 * Created on 04/12/2016.
 */
public class SQFLocalVarInStringReference implements PsiReference {
	private final SQFVariable targetVar;
	private final SQFString string;
	private final SQFScope targetVarScope;

	public SQFLocalVarInStringReference(SQFVariable targetVar, SQFScope targetVarScope, SQFString string) {
		this.targetVar = targetVar;
		this.string = string;
		this.targetVarScope = targetVarScope;
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
		if (!(element instanceof SQFVariable)) {
			return false;
		}
		SQFVariable paramVar = (SQFVariable) element;
		PsiReference[] references = paramVar.getReferences();
		for(PsiReference reference : references){
			if(reference.resolve() == paramVar && paramVar.getDeclarationScope() == targetVarScope){
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
				"targetVar=" + targetVar +
				", string=" + string +
				'}';
	}

	@Override
	public boolean isSoft() {
		return false;
	}
}
