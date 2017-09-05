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
public class SQFCodeBlock extends ASTWrapperPsiElement {
	public SQFCodeBlock(@NotNull ASTNode node) {
		super(node);
	}

	@Nullable
	public SQFLocalScope getScope() {
		return PsiTreeUtil.getChildOfType(this, SQFLocalScope.class);
	}
}
