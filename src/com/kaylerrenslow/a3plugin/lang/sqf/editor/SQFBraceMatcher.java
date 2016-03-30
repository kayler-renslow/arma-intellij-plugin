package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * Brace matching implementation for SQF language. When certain token is created by user, this will match the closing token. (Example, typing { will result in } being inserted automatically)
 * Created on 03/20/2016.
 */
public class SQFBraceMatcher implements PairedBraceMatcher{

	private final BracePair[] pairs = new BracePair[]{new BracePair(SQFTypes.LPAREN, SQFTypes.RPAREN, false), new BracePair(SQFTypes.LBRACKET, SQFTypes.RBRACKET, false), new BracePair(SQFTypes.LBRACE, SQFTypes.RBRACE, true)};

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
		PsiElement element = file.findElementAt(openingBraceOffset);
		if(element == null || element instanceof PsiFile) return openingBraceOffset;
		return openingBraceOffset;
	}
}
