package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by Kayler on 03/20/2016.
 */
public class SQFReference /*extends PsiReferenceBase<PsiElement> */implements PsiReference/* implements PsiPolyVariantReference use this interface for command calling on files*/{

	private final String variable;
	private final PsiNamedElement myElement;

	public SQFReference(PsiNamedElement element) {
		variable = element.getText();
		this.myElement = element;
	}

	@Override
	public PsiElement getElement() {
		return myElement;
	}

	@Override
	public TextRange getRangeInElement() {
		return TextRange.allOf(this.variable);
	}

	@Nullable
	@Override
	public PsiElement resolve() {
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
		if(!(element instanceof SQFNamedElement)){
			return false;
		}
		SQFNamedElement other = (SQFNamedElement) element;
		PsiElement selfResolve = resolve();
		return other.getName().equals(getCanonicalText()) && selfResolve != other;
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
		return "SQFReference{" +
				"variable='" + variable + '\'' +
				'}';
	}
}
