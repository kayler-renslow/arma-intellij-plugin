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
public class SQFCaseStatement extends ASTWrapperPsiElement implements SQFStatement {
	public SQFCaseStatement(@NotNull ASTNode node) {
		super(node);
	}

	/**
	 * @return the expression or null if doesn't exist. Will return null because of pin in grammar.
	 */
	@Nullable
	public SQFExpression getCondition() {
		return PsiTreeUtil.getChildOfType(this, SQFExpression.class);
	}


	/**
	 * @return the code block, or null if doesn't exist. Can return null because case 0; is valid
	 */
	@Nullable
	public SQFCodeBlock getBlock() {
		return PsiTreeUtil.getChildOfType(this, SQFCodeBlock.class);
	}
}
