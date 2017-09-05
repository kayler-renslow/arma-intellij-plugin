package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFParenExpression extends ASTWrapperPsiElement implements SQFExpression {
	public SQFParenExpression(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	public SQFExpression getExpresssion() {
		SQFExpression expr = PsiTreeUtil.getChildOfType(this, SQFExpression.class);
		if (expr == null) {
			throw new IllegalStateException("expr shouldn't be null");
		}
		return expr;
	}
}
