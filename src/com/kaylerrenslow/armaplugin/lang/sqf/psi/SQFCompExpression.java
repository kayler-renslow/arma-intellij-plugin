package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFCompExpression extends ASTWrapperPsiElement implements SQFBinaryExpression {
	public enum ComparisonType {
		Equals, NotEquals, LessThan, LessThanOrEqual, GreaterThan, GreaterThanOrEqual
	}

	public SQFCompExpression(@NotNull ASTNode node) {
		super(node);
	}

	@Nullable
	@Override
	public Object accept(@NotNull SQFSyntaxVisitor visitor, @NotNull CommandDescriptorCluster cluster) {
		return visitor.visit(this, cluster);
	}

	@NotNull
	public ComparisonType getComparisonType() {
		ASTNode node = getNode().findChildByType(
				TokenSet.create(
						SQFTypes.EQEQ,
						SQFTypes.NE,
						SQFTypes.LT,
						SQFTypes.LE,
						SQFTypes.GT,
						SQFTypes.GE
				)
		);
		if (node == null) {
			throw new IllegalStateException("couldn't determine comparison type");
		}
		IElementType type = node.getElementType();
		if (type == SQFTypes.EQEQ) {
			return ComparisonType.Equals;
		}
		if (type == SQFTypes.NE) {
			return ComparisonType.NotEquals;
		}
		if (type == SQFTypes.LT) {
			return ComparisonType.LessThan;
		}
		if (type == SQFTypes.LE) {
			return ComparisonType.LessThanOrEqual;
		}
		if (type == SQFTypes.GT) {
			return ComparisonType.GreaterThan;
		}
		if (type == SQFTypes.GE) {
			return ComparisonType.GreaterThanOrEqual;
		}
		throw new IllegalStateException("couldn't determine comparison type");
	}
}
