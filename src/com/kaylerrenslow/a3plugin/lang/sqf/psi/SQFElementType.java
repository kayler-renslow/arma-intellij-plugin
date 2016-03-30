package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFLanguage;

/**
 * @author Kayler
 * IElementType for SQF language
 * Created on 10/31/2015.
 */
public class SQFElementType extends IElementType{
	public SQFElementType(String debugName) {
		super(debugName, SQFLanguage.INSTANCE);
	}
}
