package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.lexer.FlexAdapter;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFLexer;

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
