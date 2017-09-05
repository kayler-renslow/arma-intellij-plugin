package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFVariable extends ASTWrapperPsiElement {
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

	@Deprecated
	public boolean isLocal() {
		//todo
		throw new UnsupportedOperationException();
	}

	@Deprecated
	public boolean isMagicVar() {
		return SQFVariableName.isMagicVar(getVarName());
	}
}
