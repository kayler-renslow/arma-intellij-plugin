package com.kaylerrenslow.a3plugin.lang.header.psi;

import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;

/**
 * IElementType for Header language
 *
 * @author Kayler
 * @since 10/31/2015
 */
public class HeaderElementType extends IElementType {
	public HeaderElementType(String debugName) {
		super(debugName, HeaderLanguage.INSTANCE);
	}
}
