package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 09/12/2017
 */
public class SQFPrivateVar {
	@NotNull
	private final SQFVariableName variableName;
	@NotNull
	private final PsiElement element;
	@NotNull
	private final SQFScope maxScope;
	@NotNull
	private final List<SQFScope> mergeScopes;

	/**
	 * @param variableName the variable name
	 * @param element      the PsiElement that contains the variable name. This is either a {@link SQFString} or {@link SQFVariable}
	 * @param maxScope     the {@link SQFScope} object that this private var exists in, as well as any children scopes in maxScope
	 * @param mergeScopes  a list of scopes that are merged with maxScope. This is only relevant for control structures. If null, means no merged scopes.
	 */
	public SQFPrivateVar(@NotNull SQFVariableName variableName, @NotNull PsiElement element, @NotNull SQFScope maxScope,
						 @Nullable List<SQFScope> mergeScopes) {
		this.variableName = variableName;
		this.element = element;
		this.maxScope = maxScope;
		this.mergeScopes = mergeScopes == null ? Collections.emptyList() : mergeScopes;
	}

	/**
	 * @return a non-mutable list of scopes that are merged with maxScope. This is only relevant for control structures.
	 * If empty, means no merged scopes.
	 */
	@NotNull
	public List<SQFScope> getMergeScopes() {
		return mergeScopes;
	}

	@NotNull
	public SQFVariableName getVariableNameObj() {
		return variableName;
	}

	/**
	 * @return the PsiElement that contains the variable name. This is either a {@link SQFString} or {@link SQFVariable}
	 */
	@NotNull
	public PsiElement getVarNameElement() {
		return element;
	}

	@Override
	public int hashCode() {
		return variableName.text().hashCode() + 31 * element.hashCode();
	}

	/**
	 * @return the {@link SQFScope} object that this private var exists in
	 */
	@NotNull
	public SQFScope getMaxScope() {
		return maxScope;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof SQFPrivateVar) {
			SQFPrivateVar other = (SQFPrivateVar) o;
			return variableName.equals(other.variableName) && maxScope == other.maxScope;
		}
		return false;
	}

	@Override
	public String toString() {
		return "SQFPrivateVar{name=" + variableName + "}";
	}
}
