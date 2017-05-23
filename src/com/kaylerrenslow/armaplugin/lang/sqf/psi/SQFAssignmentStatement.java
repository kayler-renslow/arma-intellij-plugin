package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFAssignmentStatement extends ASTWrapperPsiElement implements SQFStatement {
	public SQFAssignmentStatement(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	public SQFVariable getVariable() {
		return null;
	}

	@NotNull
	public SQFExpression getExpression() {
		return null;
	}
}
