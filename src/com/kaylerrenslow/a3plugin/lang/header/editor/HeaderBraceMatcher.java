package com.kaylerrenslow.a3plugin.lang.header.editor;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 *         PairedBraceMatcher implementation for Header language. This class takes care of figuring out what tokens to automatically close when typing. For example, if { is pressed, } will be inserted automatically
 *         Created on 03/20/2016.
 */
public class HeaderBraceMatcher implements PairedBraceMatcher {
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
		return openingBraceOffset;
	}
}
