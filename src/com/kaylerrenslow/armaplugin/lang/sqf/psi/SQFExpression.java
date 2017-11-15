package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public interface SQFExpression extends PsiElement, SQFSyntaxNode {
	/**
	 * If this {@link SQFExpression} happens to be a {@link SQFParenExpression},
	 * this method will traverse down through {@link SQFParenExpression#getExpresssion()} until the first
	 * non-{@link SQFParenExpression} is found. If this isn't a {@link SQFParenExpression}, then this will simply be returned.
	 *
	 * The point of this method is to ignore all {@link SQFParenExpression} instances so that it is easier to check
	 * if an expression has a child that is an instanceof something.
	 *
	 * @return the first {@link SQFParenExpression} found
	 */
	@NotNull
	default SQFExpression withoutParenthesis() {
		SQFExpression expr = this;
		while (expr instanceof SQFParenExpression) {
			expr = ((SQFParenExpression) expr).getExpresssion();
		}
		return expr;
	}
}
