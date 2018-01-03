package com.kaylerrenslow.armaplugin.lang.header.psi;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PairedBraceMatcher implementation for Header language.
 * This class takes care of figuring out what tokens to automatically close when typing. For example, if { is pressed, } will be inserted automatically
 *
 * @author Kayler
 * @since 03/20/2016
 */
public class HeaderBraceMatcher implements PairedBraceMatcher {
	private final BracePair[] pairs = new BracePair[]{
			new BracePair(HeaderTypes.LPAREN, HeaderTypes.RPAREN, false),
			new BracePair(HeaderTypes.LBRACE, HeaderTypes.RBRACE, true)
	};

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
	public int getCodeConstructStart(PsiFile file, int openingBraceOffset) { //TODO
		return openingBraceOffset;
	}
}

