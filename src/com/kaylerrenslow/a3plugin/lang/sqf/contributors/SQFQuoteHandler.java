package com.kaylerrenslow.a3plugin.lang.sqf.contributors;

import com.intellij.codeInsight.editorActions.QuoteHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.util.TextRange;

/**
 * @author Kayler
 * @since 04/30/2016
 */
public class SQFQuoteHandler implements QuoteHandler {
	@Override
	public boolean isClosingQuote(HighlighterIterator iterator, int offset) {
		String s = iterator.getDocument().getText(TextRange.create(offset, offset));
		System.out.println("SQFQuoteHandler.isClosingQuote s=" + s + "-");
		return iterator.getDocument().getText(TextRange.create(offset, offset)).equals("\"");
	}

	@Override
	public boolean isOpeningQuote(HighlighterIterator iterator, int offset) {
		return false;
	}

	@Override
	public boolean hasNonClosedLiteral(Editor editor, HighlighterIterator iterator, int offset) {
		return false;
	}

	@Override
	public boolean isInsideLiteral(HighlighterIterator iterator) {
		return false;
	}
}
