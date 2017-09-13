package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFString extends ASTWrapperPsiElement {
	public SQFString(@NotNull ASTNode node) {
		super(node);
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
}
