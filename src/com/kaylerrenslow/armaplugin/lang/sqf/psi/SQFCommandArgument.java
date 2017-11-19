package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
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
