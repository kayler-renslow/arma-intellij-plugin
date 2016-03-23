package com.kaylerrenslow.a3plugin.lang.sqf.psi.impl.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 03/20/2016.
 */
public class SQFVariableReference implements PsiReference{

	private final String variable;
	private final PsiNamedElement myElement;
	private final IElementType myVariableElementType;

	public SQFVariableReference(PsiNamedElement element, IElementType myVariableElementType) {
		variable = element.getText();
		this.myElement = element;
		this.myVariableElementType = myVariableElementType;
	}

	@Override
	public PsiElement getElement() {
		return myElement;
	}

	@Override
	public TextRange getRangeInElement() {
		return TextRange.allOf(this.getCanonicalText());
	}


	@Nullable
	@Override
	public PsiElement resolve() {
//		System.out.println("SQFVariableReference.resolve");
		return this.myElement;
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return myElement.getName();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		return myElement.setName(newElementName);
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		if(!(element instanceof SQFVariableNamedElement)){
			return false;
		}
		if(myVariableElementType == SQFTypes.LANG_VAR){
			return false;
		}
		SQFVariableNamedElement other = (SQFVariableNamedElement) element;
		PsiElement selfResolve = resolve();
		boolean referenceTo =other.getName().equals(getCanonicalText()) && selfResolve != other;
		if(myVariableElementType == SQFTypes.LOCAL_VAR){
			return referenceTo && other.getContainingFile() == selfResolve.getContainingFile();
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
		return true;
	}

	@Override
	public String toString() {
		return "SQFVariableReference{" +
				"variable='" + variable + '\'' +
				'}';
	}
}
