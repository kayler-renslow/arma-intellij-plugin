package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.NotNull;

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

	/**
	 * @param variableName the variable name
	 * @param element      the PsiElement that contains the variable name. This is either a {@link SQFString} or {@link SQFVariable}
	 * @param maxScope     the {@link SQFScope} object that this private var exists in, as well as any children scopes in maxScope
	 */
	public SQFPrivateVar(@NotNull SQFVariableName variableName, @NotNull PsiElement element, @NotNull SQFScope maxScope) {
		this.variableName = variableName;
		this.element = element;
		this.maxScope = maxScope;
	}

	@NotNull
	public SQFVariableName getVariableNameObj() {
		return variableName;
	}

	/**
	 * @return the PsiElement that contains the variable name. This is either a {@link SQFString} or {@link SQFVariable}
	 */
	@NotNull
	public PsiElement getElement() {
		return element;
	}

	@Override
	public int hashCode() {
		return variableName.text().hashCode() + 31 * element.hashCode();
	}

	/**
	 * @return the {@link SQFScope} object that this private var exists in, as well as any children scopes in maxScope
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
			return variableName.equals(other.variableName) && element.equals(other.element);
		}
		return false;
	}
}
