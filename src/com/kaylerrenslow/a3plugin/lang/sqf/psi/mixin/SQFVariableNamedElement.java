package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.tree.IElementType;

/**
 * Created by Kayler on 03/19/2016.
 */
public interface SQFVariableNamedElement extends PsiNameIdentifierOwner, SQFVariableBase{
	IElementType getVariableType();
}
