package com.kaylerrenslow.a3plugin.lang.sqf;

import com.intellij.lexer.FlexAdapter;

/**
 * @author Kayler
 * Creates a new SQF JFlex lexer
 * Created on 10/31/2015.
 */
public class SQFLexerAdapter extends FlexAdapter{
	public SQFLexerAdapter() {
		super(new SQFLexer(null));
	}

}
