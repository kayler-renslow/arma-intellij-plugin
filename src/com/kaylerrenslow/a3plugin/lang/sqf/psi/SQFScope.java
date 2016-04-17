package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;

import java.util.List;

/**
 * @author Kayler
 * Base interface for scopes in SQF. See the grammar file for where it's used
 * Created on 03/24/2016.
 */
public interface SQFScope extends PsiElement{
	List<SQFPrivateDeclVar> getPrivateDeclaredVars();
	Key<String[]> KEY_ITERATION_VARS = new Key<>("KEY_ITERATION_VARS");
}
