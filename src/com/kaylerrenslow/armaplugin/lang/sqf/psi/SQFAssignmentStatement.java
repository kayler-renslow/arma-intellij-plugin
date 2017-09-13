package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
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
	public SQFVariable getVar() {
		SQFVariable var = PsiTreeUtil.getChildOfType(this, SQFVariable.class);
		if (var == null) {
			throw new IllegalStateException("var shouldn't be null");
		}
		return var;
	}

	/**
	 * @return the {@link SQFExpression} instance, or will return null since assignment can be matched as soon as = is discovered
	 */
	@Nullable
	public SQFExpression getExpr() {
		return PsiTreeUtil.getChildOfType(this, SQFExpression.class);
	}

	/**
	 * @return true if the assignment starts with "private", false otherwise
	 */
	public boolean isPrivate() {
		for (PsiElement child : getChildren()) {
			if (child.getText().equalsIgnoreCase("private")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the expression or null if doesn't exist. Will return null because of pin in grammar.
	 */
	@Nullable
	public SQFPsiExpression getExpression() {
		return PsiTreeUtil.getChildOfType(this, SQFPsiExpression.class);
	}
}
