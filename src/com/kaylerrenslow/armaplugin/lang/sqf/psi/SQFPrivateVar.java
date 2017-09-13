package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import com.intellij.structuralsearch.plugin.util.SmartPsiPointer;
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
	private final SmartPsiPointer elementPointer;

	/**
	 * @param variableName the variable name
	 * @param element      the PsiElement that contains the variable name. This is either a {@link SQFString} or {@link SQFVariable}
	 */
	public SQFPrivateVar(@NotNull SQFVariableName variableName, @NotNull PsiElement element) {
		this.variableName = variableName;
		this.elementPointer = new SmartPsiPointer(element);
	}

	@NotNull
	public SQFVariableName getVariableName() {
		return variableName;
	}

	@NotNull
	public SmartPsiPointer getElementPointer() {
		return elementPointer;
	}

	@Override
	public int hashCode() {
		return variableName.text().hashCode() + 31 * elementPointer.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof SQFPrivateVar) {
			SQFPrivateVar other = (SQFPrivateVar) o;
			return variableName.equals(other.variableName) && elementPointer.equals(other.elementPointer);
		}
		return false;
	}
}
