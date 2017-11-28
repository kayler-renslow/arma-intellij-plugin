package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @since 09/12/2017
 */
public interface SQFPrivateVar {

	/**
	 * @return a non-mutable list of scopes that are merged with maxScope. This is only relevant for control structures.
	 * If empty, means no merged scopes.
	 */
	@NotNull
	List<SQFScope> getMergeScopes();

	@NotNull
	SQFVariableName getVariableNameObj();

	/**
	 * @return the PsiElement that contains the variable name. This is either a {@link SQFString} or {@link SQFVariable}
	 */
	@NotNull
	PsiElement getVarNameElement();

	/**
	 * @return the {@link SQFScope} object that this private var exists in
	 */
	@NotNull
	SQFScope getMaxScope();
}
