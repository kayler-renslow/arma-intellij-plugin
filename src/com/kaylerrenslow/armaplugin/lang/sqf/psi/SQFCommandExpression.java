package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFCommandExpression extends ASTWrapperPsiElement implements SQFExpression {
	public SQFCommandExpression(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	public SQFCommand getSQFCommand() {
		SQFCommand command = PsiTreeUtil.getChildOfType(this, SQFCommand.class);
		if (command == null) {
			throw new IllegalStateException("command shouldn't be null");
		}
		return command;
	}
}
