package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFVariableInStringReference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFString extends ASTWrapperPsiElement /*implements PsiNamedElement*/ {
	public SQFString(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		List<SQFVariableInStringReference> currentFileRefs = SQFScope.getVariableReferencesFor(this);
		PsiReference[] refsFromProviders = ReferenceProvidersRegistry.getReferencesFromProviders(this);
		if (currentFileRefs.size() == 0 && refsFromProviders.length == 0) {
			return PsiReference.EMPTY_ARRAY;
		}
		PsiReference[] refsAsArray = new PsiReference[currentFileRefs.size() + refsFromProviders.length];
		int i = 0;
		for (; i < currentFileRefs.size(); i++) {
			refsAsArray[i] = currentFileRefs.get(i);
		}
		for (int j = 0; j < refsFromProviders.length; i++, j++) {
			refsAsArray[i] = refsFromProviders[j];
		}
		return refsAsArray;
	}

	@Override
	public PsiReference getReference() {
		PsiReference[] refs = getReferences();
		if (refs.length == 0) {
			return null;
		}
		return refs[0];
	}

	/**
	 * @return TextRange that doesn't include the quotes and the range is relative to the <b>file</b>
	 * @see #getNonQuoteRangeRelativeToElement()
	 */
	@NotNull
	public TextRange getNonQuoteRangeRelativeToFile() {
		return TextRange.from(getTextOffset() + 1, getTextLength() - 2);
	}

	/**
	 * @return TextRange that doesn't include the quotes and the range is relative to the <b>element</b>
	 * @see #getNonQuoteRangeRelativeToFile()
	 */
	@NotNull
	public TextRange getNonQuoteRangeRelativeToElement() {
		return TextRange.from(1, getTextLength() - 2);
	}

	/**
	 * @return the contents of the String without the outermost quotes (this will include any cancelled quotes inside)
	 */
	@NotNull
	public String getNonQuoteText() {
		return getText().substring(1, getTextLength() - 1);
	}

	/**
	 * @return true if the String contains a local variable (_var for instance), or false if it doesn't
	 */
	public boolean containsLocalVariable() {
		String nonq = getNonQuoteText();
		return nonq.startsWith("_") && !nonq.contains(" ");
	}
}
