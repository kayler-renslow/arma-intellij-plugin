package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFVariableReference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFVariable extends ASTWrapperPsiElement implements PsiNamedElement {
	public SQFVariable(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	public SQFVariableName getVarNameObj() {
		return new SQFVariableName(getVarName());
	}

	@NotNull
	public String getVarName() {
		return getText();
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		List<SQFVariableReference> references = SQFScope.getVariableReferencesFor(this);
		return references.toArray(new PsiReference[references.size()]);
	}

	/**
	 * @return true if the variable starts with _, false otherwise
	 */
	public boolean isLocal() {
		return getVarName().charAt(0) == '_';
	}

	/**
	 * @return true if the variable is a magic variable ({@link SQFVariableName#isMagicVar(String)}), false otherwise
	 */
	public boolean isMagicVar() {
		return SQFVariableName.isMagicVar(getVarName());
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		return null;
	}

	@Override
	public String getName() {
		return getVarName();
	}
}
