package com.kaylerrenslow.a3plugin.lang.shared.stringtable.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 @author Kayler
 Creates a reference that points from SQF string to stringtable.xml key attribute
 Created on 06/25/2016. */
public class StringtableSQFStringReference implements PsiReference {
	private final SQFString string;
	private final XmlAttributeValue keyAttributeValue;

	public StringtableSQFStringReference(SQFString string, XmlAttributeValue keyAttributeValue) {
		this.string = string;
		this.keyAttributeValue = keyAttributeValue;
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
		return keyAttributeValue;
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		XmlAttributeValue resolve = (XmlAttributeValue) resolve();
		if (resolve == null) {
			throw new RuntimeException("u wot m8");
		}
		return resolve.getValue();
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
