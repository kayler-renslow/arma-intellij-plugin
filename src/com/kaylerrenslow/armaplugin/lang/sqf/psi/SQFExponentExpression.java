package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFExponentExpression extends ASTWrapperPsiElement implements SQFExpression {
	public SQFExponentExpression(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	public List<SQFExpression> getExprList() {
		return PsiTreeUtil.getChildrenOfTypeAsList(this, SQFExpression.class);
	}
}
