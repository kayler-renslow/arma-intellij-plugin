package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.tree.IElementType;

/**
 * @author Kayler
 * @since 03/19/2016
 */
public interface SQFVariableNamedElement extends PsiNameIdentifierOwner, SQFVariableBase {

	/**
	 * Get IElementType of the SQFVariable PsiElement
	 */
	IElementType getVariableType();

	/**
	 * Return true if the variable is a global variable, false otherwise
	 */
	boolean isGlobalVariable();

	/**
	 * Return true if the variable is a lang variable (_x, _this, _exception), false otherwise
	 */
	boolean isLangVar();
}
