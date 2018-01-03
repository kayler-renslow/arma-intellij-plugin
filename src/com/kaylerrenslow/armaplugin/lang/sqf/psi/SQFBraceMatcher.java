package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Brace matching implementation for SQF language.
 * When certain token is created by user, this will match the closing token. (Example, typing { will result in } being inserted automatically)
 *
 * @author Kayler
 * @since 03/20/2016
 */
public class SQFBraceMatcher implements PairedBraceMatcher {

	private final BracePair[] pairs = new BracePair[]{
			new BracePair(SQFTypes.LPAREN, SQFTypes.RPAREN, false),
			new BracePair(SQFTypes.L_CURLY_BRACE, SQFTypes.R_CURLY_BRACE, false),
			new BracePair(SQFTypes.L_SQ_BRACKET, SQFTypes.R_SQ_BRACKET, true)};

	@NotNull
	@Override
	public BracePair[] getPairs() {
		return pairs;
	}

	@Override
	public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
		return true;
	}

	@Override
	public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
		if (file == null) {
			return openingBraceOffset;
		}
		PsiElement element = file.findElementAt(openingBraceOffset);
		if (element == null || element instanceof PsiFile) return openingBraceOffset;
		return openingBraceOffset;
	}
}
