package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFModExpression extends ASTWrapperPsiElement implements SQFBinaryExpression {
	public SQFModExpression(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	@Override
	public Object accept(@NotNull SQFSyntaxVisitor visitor, @NotNull CommandDescriptorCluster cluster) {
		return visitor.visit(this, cluster);
	}
}
