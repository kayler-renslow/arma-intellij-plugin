package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFScope;

/**
 * Created by Kayler on 04/14/2016.
 */
public interface SQFForLoopBase extends PsiElement{
	SQFScope getLoopScope();
	String[] getIterationVariables();
}
