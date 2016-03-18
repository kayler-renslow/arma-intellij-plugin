package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFLanguage;

/**
 * Created by Kayler on 10/31/2015.
 */
public class SQFElementType extends IElementType{
	public SQFElementType(String debugName) {
		super(debugName, SQFLanguage.INSTANCE);
	}
}
