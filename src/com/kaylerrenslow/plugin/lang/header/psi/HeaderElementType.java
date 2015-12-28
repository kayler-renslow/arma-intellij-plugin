package com.kaylerrenslow.plugin.lang.header.psi;

import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.plugin.lang.sqf.SQFLanguage;

/**
 * Created by Kayler on 10/31/2015.
 */
public class HeaderElementType extends IElementType{
	public HeaderElementType(String debugName) {
		super(debugName, SQFLanguage.INSTANCE);
	}
}
