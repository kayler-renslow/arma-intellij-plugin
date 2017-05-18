package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFLanguage;

/**
 * IElementType for SQF language.
 * An IElementType is similar to TokenType, but IElementType is for the parser and TokenType is for the lexer.
 *
 * @author Kayler
 * @since 10/31/2015
 * @see SQFTokenType
 */
public class SQFElementType extends IElementType {
	public SQFElementType(String debugName) {
		super(debugName, SQFLanguage.INSTANCE);
	}
}
