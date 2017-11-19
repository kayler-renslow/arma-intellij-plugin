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
public class SQFLiteralExpression extends ASTWrapperPsiElement implements SQFExpression {
	public SQFLiteralExpression(@NotNull ASTNode node) {
		super(node);
	}

	@Nullable
	public SQFString getStr() {
		return PsiTreeUtil.getChildOfType(this, SQFString.class);
	}

	@Nullable
	public SQFVariable getVar() {
		return PsiTreeUtil.getChildOfType(this, SQFVariable.class);
	}

	@Nullable
	public SQFArray getArr() {
		return PsiTreeUtil.getChildOfType(this, SQFArray.class);
	}

	@Nullable
	public SQFNumber getNum() {
		return PsiTreeUtil.getChildOfType(this, SQFNumber.class);
	}

	@NotNull
	@Override
	public Object accept(@NotNull SQFSyntaxVisitor visitor, @NotNull CommandDescriptorCluster cluster) {
		return visitor.visit(this, cluster);
	}
}
