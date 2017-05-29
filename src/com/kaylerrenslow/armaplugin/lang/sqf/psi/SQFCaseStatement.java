package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
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

	//can return null because of pin
	@Nullable
	public SQFExpression getCondition() {
		return null;
	}


	//can return null because case 0; is valid
	@Nullable
	public SQFCodeBlock getCodeBlock() {
		return null;
	}
}
