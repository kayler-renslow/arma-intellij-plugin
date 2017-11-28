package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used for storing either a {@link SQFCodeBlock} XOR a {@link SQFExpression}
 *
 * @author Kayler
 * @since 09/19/2017
 */
public interface SQFBlockOrExpression {

	/**
	 * @return a {@link SQFCodeBlock} if {@link #getExpr()} is null
	 */
	@Nullable
	SQFCodeBlock getBlock();

	/**
	 * @return a {@link SQFExpression} if {@link #getBlock()} is null
	 */
	@Nullable
	SQFExpression getExpr();

	/**
	 * @return a {@link ASTNode} for either the {@link #getBlock()} XOR {@link #getExpr()}.
	 */
	@NotNull
	default ASTNode getNode() {
		SQFCodeBlock block = getBlock();
		if (block == null) {
			SQFExpression expr = getExpr();
			if (expr == null) {
				throw new IllegalStateException("both block and expr are null");
			}
			return expr.getNode();
		}
		return block.getNode();
	}

	class Impl implements SQFBlockOrExpression {
		@Nullable
		private final SQFCodeBlock block;
		@Nullable
		private final SQFExpression expression;

		public Impl(@Nullable SQFCodeBlock block) {
			this.block = block;
			this.expression = null;
		}

		public Impl(@Nullable SQFExpression expression) {
			this.block = null;
			this.expression = expression;
		}

		@Override
		@Nullable
		public SQFCodeBlock getBlock() {
			if (block != null) {
				return block;
			}
			if (expression instanceof SQFCodeBlockExpression) {
				SQFCodeBlockExpression expr = (SQFCodeBlockExpression) expression;
				return expr.getBlock();
			}
			return null;
		}

		@Override
		@Nullable
		public SQFExpression getExpr() {
			return expression;
		}
	}
}
