package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	//will return null since assignment can be matched as soon as = is discovered
	@Nullable
	public SQFExpression getExpression() {
		return null;
	}

	public boolean isPrivate() {
		//todo
		throw new UnsupportedOperationException();
	}
}
