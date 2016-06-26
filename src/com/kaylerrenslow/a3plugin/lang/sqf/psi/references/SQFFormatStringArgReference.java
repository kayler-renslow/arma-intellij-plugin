package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFArrayEntry;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 @author Kayler
 Creates a reference between a format String argument and the array entry (e.g. format["%1", (1+2)] will create a reference between '%1' and '(1+2)')
 Created on 06/26/2016.
 */
public class SQFFormatStringArgReference implements PsiReference{
	private final SQFString formatString;
	private final TextRange rangeInString;
	private final SQFArrayEntry targetArgument;

	public SQFFormatStringArgReference(SQFString formatString, TextRange rangeInString, SQFArrayEntry targetArgument) {
		this.formatString = formatString;
		this.rangeInString = rangeInString;
		this.targetArgument = targetArgument;
	}

	@Override
	public PsiElement getElement() {
		return formatString;
	}

	@Override
	public TextRange getRangeInElement() {
		return rangeInString;
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		return targetArgument;
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return targetArgument.getText();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		return null;
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		System.out.println("SQFFormatStringArgReference.isReferenceTo element = " + element.getText());
		return element == resolve();
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
