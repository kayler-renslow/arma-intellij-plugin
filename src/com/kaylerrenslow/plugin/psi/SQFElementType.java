package com.kaylerrenslow.plugin.psi;

import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.plugin.SQFLanguage;

/**
 * Created by Kayler on 10/31/2015.
 */
public class SQFElementType extends IElementType{
	public SQFElementType(String debugName) {
		super(debugName, SQFLanguage.INSTANCE);
	}
}
