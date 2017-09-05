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
	 * @deprecated un-deprecate this when implemented
	 */
	@Deprecated
	public boolean isPrivate() {
		//todo
		throw new UnsupportedOperationException();
	}
}
