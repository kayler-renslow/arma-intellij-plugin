package com.kaylerrenslow.plugin.lang.header;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 * Created by Kayler on 10/31/2015.
 */
public class HeaderLexerAdapter extends FlexAdapter{
	public HeaderLexerAdapter() {
		super(new HeaderLexer((Reader) null));
	}
}
