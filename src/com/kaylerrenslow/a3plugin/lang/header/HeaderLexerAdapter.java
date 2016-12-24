package com.kaylerrenslow.a3plugin.lang.header;

import com.intellij.lexer.FlexAdapter;

/**
 * Creates JFlex lexer for Header language
 *
 * @author Kayler
 * @since 10/31/2015
 */
public class HeaderLexerAdapter extends FlexAdapter {
	public HeaderLexerAdapter() {
		super(new HeaderLexer(null));
	}
}
