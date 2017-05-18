package com.kaylerrenslow.armaplugin.lang.sqf;

import com.intellij.lexer.FlexAdapter;

/**
 * Creates a new SQF JFlex lexer
 *
 * @author Kayler
 * @since 10/31/2015
 */
public class SQFLexerAdapter extends FlexAdapter {
	public SQFLexerAdapter() {
		super(new SQFLexer(null));
	}

}
