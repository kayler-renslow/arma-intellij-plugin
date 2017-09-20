package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * @since 09/19/2017
 */
public interface SQFBlockOrExpression {

	@Nullable
	SQFCodeBlock getBlock();

	@Nullable
	SQFExpression getExpr();

	class Impl implements SQFBlockOrExpression {
		@Nullable
		private final SQFCodeBlock block;
		@Nullable
		private final SQFExpression expression;

		public Impl(@Nullable SQFCodeBlock block, @Nullable SQFExpression expression) {
			this.block = block;
			this.expression = expression;
		}

		@Override
		@Nullable
		public SQFCodeBlock getBlock() {
			return block;
		}

		@Override
		@Nullable
		public SQFExpression getExpr() {
			return expression;
		}
	}
}
