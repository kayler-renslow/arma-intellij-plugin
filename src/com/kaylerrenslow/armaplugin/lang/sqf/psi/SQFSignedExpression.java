package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFSignedExpression extends ASTWrapperPsiElement implements SQFUnaryExpression {
	public enum Sign {
		Plus, Minus
	}

	public SQFSignedExpression(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	public Sign getSign() {
		String t = getText();
		if (t == null || t.length() == 0) {
			throw new IllegalStateException("couldn't determine sign");
		}
		if (t.charAt(0) == '+') {
			return Sign.Plus;
		}
		if (t.charAt(0) == '-') {
			return Sign.Plus;
		}
		throw new IllegalStateException("couldn't determine sign");
	}

	@NotNull
	@Override
	public SQFExpression getExpr() {
		SQFExpression expr = SQFUnaryExpression.super.getExpr();
		if (expr == null) {
			throw new IllegalStateException("expr is null for " + getText());
		}
		return expr;
	}

	@NotNull
	@Override
	public Object accept(@NotNull SQFSyntaxVisitor visitor, @NotNull CommandDescriptorCluster cluster) {
		return visitor.visit(this, cluster);
	}
}
