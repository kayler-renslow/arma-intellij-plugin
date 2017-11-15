package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFQuestStatement extends ASTWrapperPsiElement implements SQFStatement {
	public SQFQuestStatement(@NotNull ASTNode node) {
		super(node);
	}

	/**
	 * @return the condition ("? CONDITION : IF_TRUE_EXPR"), or null if it doesn't
	 * exist because of grammar pinning.
	 */
	@Nullable
	public SQFExpression getCondition() {
		SQFExpression[] children = PsiTreeUtil.getChildrenOfType(this, SQFExpression.class);
		if (children == null || children.length == 0) {
			return null;
		}
		return children[0];
	}

	/**
	 * @return the expression executed in condition is true ("? CONDITION : IF_TRUE_EXPR"), or null if it doesn't
	 * exist because of grammar pinning.
	 */
	@Nullable
	public SQFExpression getIfTrueExpr() {
		SQFExpression[] children = PsiTreeUtil.getChildrenOfType(this, SQFExpression.class);
		if (children == null || children.length < 2) {
			return null;
		}
		return children[1];
	}

	@Nullable
	@Override
	public Object accept(@NotNull SQFSyntaxVisitor visitor, @NotNull CommandDescriptorCluster cluster) {
		return visitor.visit(this, cluster);
	}
}
