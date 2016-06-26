package com.kaylerrenslow.a3plugin.lang.header.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderStringtableKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 @author Kayler
 Creates a reference that points from Header StringtableKey to stringtable key attribute in stringtable.xml
 Created on 06/25/2016. */
public class HeaderStringtableKeyReference implements PsiReference {
	private final HeaderStringtableKey key;
	private final XmlAttributeValue keyAttributeValue;

	public HeaderStringtableKeyReference(HeaderStringtableKey key, XmlAttributeValue keyAttributeValue) {
		this.key = key;
		this.keyAttributeValue = keyAttributeValue;
	}

	@Override
	public PsiElement getElement() {
		return key;
	}

	@Override
	public TextRange getRangeInElement() {
		return TextRange.create(1, key.getTextLength());
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
		return key.setName(newElementName);
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
