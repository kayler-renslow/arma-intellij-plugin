package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Denotes an argument to a {@link SQFCommandExpression}, however, this will never contain a {@link SQFCommandExpression} itself
 * (i.e. {@link #getExpr()} will never return an instance of {@link SQFCommandExpression}). However,
 * you should note that the expression can be a {@link SQFParenExpression}.
 *
 * @author Kayler
 * @since 09/13/2017
 */
public class SQFCommandArgument extends ASTWrapperPsiElement implements SQFBlockOrExpression {
	public SQFCommandArgument(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	@Nullable
	public SQFCodeBlock getBlock() {
		SQFCodeBlockExpression codeBlockExpression = PsiTreeUtil.getChildOfType(this, SQFCodeBlockExpression.class);
		if (codeBlockExpression != null) {
			return codeBlockExpression.getBlock();
		}
		return PsiTreeUtil.getChildOfType(this, SQFCodeBlock.class);
	}

	@Override
	@NotNull
	public SQFExpression getExpr() {
		SQFExpression expr = PsiTreeUtil.getChildOfType(this, SQFExpression.class);
		if (expr == null) {
			throw new IllegalStateException("expression was null for " + getText());
		}
		return expr;
	}
}
