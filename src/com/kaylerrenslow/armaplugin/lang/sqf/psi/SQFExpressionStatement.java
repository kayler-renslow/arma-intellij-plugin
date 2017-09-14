package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 09/13/2017
 */
public class SQFExpressionStatement extends ASTWrapperPsiElement implements SQFStatement {
	public SQFExpressionStatement(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	public SQFExpression getExpr() {
		SQFExpression expression = PsiTreeUtil.getChildOfType(this, SQFExpression.class);
		if (expression == null) {
			throw new IllegalStateException("expression shouldn't be null");
		}
		return expression;
	}

	/**
	 * @return the expression or null if doesn't exist.
	 */
	@NotNull
	public SQFPsiExpression getExpression() {
		SQFPsiExpression expression = PsiTreeUtil.getChildOfType(this, SQFPsiExpression.class);
		if (expression == null) {
			throw new IllegalStateException("expression shouldn't be null");
		}
		return expression;
	}
}
