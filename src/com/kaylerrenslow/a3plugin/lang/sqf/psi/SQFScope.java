package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization.SQFPrivatizer;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateDeclVar;

import java.util.List;

/**
 * @author Kayler
 * Base interface for scopes in SQF. See the grammar file for where it's used
 * Created on 03/24/2016.
 */
public interface SQFScope extends PsiElement, SQFPrivatizer{
	List<SQFStatement> getStatementsForScope();
	List<SQFPrivateDeclVar> getPrivateDeclaredVars();
	boolean checkIfSpawn();
	Key<String[]> KEY_ITERATION_VARS = new Key<>("KEY_ITERATION_VARS");
}
