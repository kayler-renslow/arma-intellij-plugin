package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import javax.annotation.Nullable;

/**
 * A base interface that provides default implementations for retrieving left and right args.
 *
 * @author Kayler
 * @since 05/23/2017
 */
public interface SQFBinaryExpression extends PsiElement, SQFExpression {
	@Nullable
	default SQFExpression getLeft() {
		SQFExpression[] arr = PsiTreeUtil.getChildrenOfType(this, SQFExpression.class);
		return (arr != null && arr.length > 0) ? arr[0] : null;
	}

	@Nullable
	default SQFExpression getRight() {
		SQFExpression[] arr = PsiTreeUtil.getChildrenOfType(this, SQFExpression.class);
		return (arr != null && arr.length > 1) ? arr[1] : null;
	}
}
