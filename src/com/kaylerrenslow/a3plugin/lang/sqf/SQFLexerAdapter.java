package com.kaylerrenslow.a3plugin.lang.sqf;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 * @author Kayler
 * Creates a new SQF JFlex lexer
 * Created on 10/31/2015.
 */
public class SQFLexerAdapter extends FlexAdapter{
	public SQFLexerAdapter() {
		super(new SQFLexer((Reader) null));
	}

}
