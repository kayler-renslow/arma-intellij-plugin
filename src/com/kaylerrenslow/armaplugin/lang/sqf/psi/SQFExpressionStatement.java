package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	@Nullable
	@Override
	public Object accept(@NotNull SQFSyntaxVisitor visitor, @NotNull CommandDescriptor[] descriptors) {
		return visitor.visit(this, descriptors);
	}
}
