package com.kaylerrenslow.a3plugin.lang.header;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 * @author Kayler
 * Creates JFlex lexer for Header language
 * Created on 10/31/2015.
 */
public class HeaderLexerAdapter extends FlexAdapter{
	public HeaderLexerAdapter() {
		super(new HeaderLexer((Reader) null));
	}
}
