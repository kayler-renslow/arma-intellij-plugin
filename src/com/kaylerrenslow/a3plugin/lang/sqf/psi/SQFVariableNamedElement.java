package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.tree.IElementType;

/**
 * Created by Kayler on 03/19/2016.
 */
public interface SQFVariableNamedElement extends PsiNameIdentifierOwner{
	IElementType getVariableType();
}
