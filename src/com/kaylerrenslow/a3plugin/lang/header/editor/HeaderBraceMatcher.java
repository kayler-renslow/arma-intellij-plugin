package com.kaylerrenslow.a3plugin.lang.header.editor;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 03/20/2016.
 */
public class HeaderBraceMatcher implements PairedBraceMatcher{
	private final BracePair[] pairs = new BracePair[]{new BracePair(HeaderTypes.LPAREN, HeaderTypes.RPAREN, false), new BracePair(HeaderTypes.LBRACE, HeaderTypes.RBRACE, true)};

	@Override
	public BracePair[] getPairs() {
		return pairs;
	}

	@Override
	public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
		return true;
	}

	@Override
	public int getCodeConstructStart(PsiFile file, int openingBraceOffset) { //TODO
		PsiElement element = file.findElementAt(openingBraceOffset);
		if(element == null || element instanceof PsiFile) return openingBraceOffset;
		return openingBraceOffset;
	}
}
