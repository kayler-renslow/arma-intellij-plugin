package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * @since 11/14/2017
 */
public interface SQFUnaryExpression extends SQFExpression {

	@Nullable
	default SQFExpression getExpr() {
		return PsiTreeUtil.getChildOfType(this, SQFExpression.class);
	}

}
