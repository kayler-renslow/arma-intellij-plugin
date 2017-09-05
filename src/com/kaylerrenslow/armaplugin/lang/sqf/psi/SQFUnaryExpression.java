package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFUnaryExpression extends ASTWrapperPsiElement implements SQFExpression {
	public SQFUnaryExpression(@NotNull ASTNode node) {
		super(node);
	}

	@Nullable
	public SQFExpression getExpression() {
		return PsiTreeUtil.getChildOfType(this, SQFExpression.class);
	}
}
