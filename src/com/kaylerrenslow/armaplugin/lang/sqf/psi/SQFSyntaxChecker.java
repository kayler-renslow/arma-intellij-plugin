package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.ValueType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * @author Kayler
 * @since 11/14/2017
 */
public class SQFSyntaxChecker implements SQFSyntaxVisitor<ValueType> {
	@NotNull
	private final List<SQFStatement> statements;
	@NotNull
	private final CommandDescriptorCluster cluster;
	@NotNull
	private final ProblemsHolder problems;

	public SQFSyntaxChecker(@NotNull List<SQFStatement> statements, @NotNull CommandDescriptorCluster cluster,
							@NotNull ProblemsHolder holder) {
		this.statements = statements;
		this.cluster = cluster;
		this.problems = holder;
	}

	/**
	 * @return the last statement's resulted {@link ValueType}.
	 * If there was no statements to check, will return {@link ValueType#NOTHING}
	 */
	@Nullable
	public ValueType begin() {
		ValueType ret = ValueType.NOTHING;
		for (SQFStatement statement : statements) {
			ret = (ValueType) statement.accept(this, cluster);
		}
		return ret;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFScope scope, @NotNull CommandDescriptorCluster cluster) {
		List<SQFStatement> statements = scope.getChildStatements();
		ValueType ret = ValueType.NOTHING;
		for (SQFStatement statement : statements) {
			ret = (ValueType) statement.accept(this, cluster);
		}
		return ret;
	}


	@Nullable
	@Override
	public ValueType visit(@NotNull SQFAssignmentStatement statement, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression expr = statement.getExpr();
		if (expr != null) {
			expr.accept(this, cluster);
		}

		return ValueType.NOTHING;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFCaseStatement statement, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression cond = statement.getCondition();
		if (cond != null) {
			cond.accept(this, cluster);
		}
		SQFCodeBlock block = statement.getBlock();
		if (block != null) {
			SQFLocalScope scope = block.getScope();
			if (scope != null) {
				scope.accept(this, cluster);
			}
		}

		return ValueType.NOTHING;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFExpressionStatement statement, @NotNull CommandDescriptorCluster cluster) {
		return (ValueType) statement.getExpr().accept(this, cluster);
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFQuestStatement statement, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression cond = statement.getCondition();
		if (cond != null) {
			cond.accept(this, cluster);
		}
		SQFExpression ifTrue = statement.getIfTrueExpr();
		if (ifTrue != null) {
			ifTrue.accept(this, cluster);
		}

		return ValueType.NOTHING;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFAddExpression expr, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression leftExpr = expr.getLeft();
		ValueType left = null, right = null;
		if (leftExpr != null) {
			left = (ValueType) leftExpr.accept(this, cluster);
		}

		SQFExpression rightExpr = expr.getRight();
		if (rightExpr != null) {
			right = (ValueType) rightExpr.accept(this, cluster);
		}

		if (left == null || right == null) {
			//can't be determined
			return ValueType.ANYTHING;
		}

		ValueType[] allowedTypes = {
				ValueType.NUMBER,
				ValueType.STRING,
				ValueType.ARRAY
		};

		switch (left) {
			case NUMBER: //fall
			case STRING: {
				assertIsType(right, left, rightExpr);
				break;
			}
			case _VARIABLE: {
				assertIsType(right, allowedTypes, rightExpr);
				return ValueType._VARIABLE;
			}
			default: {
				if (left.isArray() && right == ValueType._VARIABLE) {
					return ValueType.ARRAY;
				}
				if (left.isArray() && right.isArray()) {
					return ValueType.ARRAY;
				}
				notOfType(allowedTypes, right, rightExpr);
				return ValueType.ANYTHING;
			}
		}

		return ValueType.NUMBER;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFSubExpression expr, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression leftExpr = expr.getLeft();
		ValueType left = null, right = null;
		if (leftExpr != null) {
			left = (ValueType) leftExpr.accept(this, cluster);
		}

		SQFExpression rightExpr = expr.getRight();
		if (rightExpr != null) {
			right = (ValueType) rightExpr.accept(this, cluster);
		}

		if (left == null || right == null) {
			//can't be determined
			return ValueType.ANYTHING;
		}

		ValueType[] allowedTypes = {
				ValueType.NUMBER,
				ValueType.ARRAY
		};

		switch (left) {
			case NUMBER: {
				assertIsType(right, ValueType.NUMBER, rightExpr);
				break;
			}
			case _VARIABLE: {
				assertIsType(right, allowedTypes, rightExpr);
				return ValueType._VARIABLE;
			}
			default: {
				if (left.isArray() && !right.isArray()) {
					problems.registerProblem(rightExpr, "Not an Array type.", ProblemHighlightType.ERROR);
					return ValueType.ARRAY;
				}
				notOfType(allowedTypes, right, rightExpr);
				return ValueType.ANYTHING;
			}
		}

		return left;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFMultExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return binaryExprSameTypeHelper(expr, ValueType.NUMBER, cluster);
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFDivExpression expr, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression leftExpr = expr.getLeft();
		ValueType left = null, right = null;
		if (leftExpr != null) {
			left = (ValueType) leftExpr.accept(this, cluster);
		}

		SQFExpression rightExpr = expr.getRight();
		if (rightExpr != null) {
			right = (ValueType) rightExpr.accept(this, cluster);
		}

		if (left == null || right == null) {
			//can't be determined
			return ValueType.ANYTHING;
		}

		ValueType[] allowedTypes = {
				ValueType.NUMBER,
				ValueType.STRING
		};

		switch (left) {
			case NUMBER: {
				assertIsType(right, ValueType.NUMBER, rightExpr);
				return ValueType.NUMBER;
			}
			case CONFIG: {
				assertIsType(right, ValueType.STRING, rightExpr);
				return ValueType.CONFIG;
			}
			case _VARIABLE: {
				assertIsType(right, allowedTypes, rightExpr);
				return ValueType._VARIABLE;
			}
			default: {
				notOfType(allowedTypes, right, rightExpr
				);
				return ValueType.ANYTHING;
			}
		}
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFModExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return binaryExprSameTypeHelper(expr, ValueType.NUMBER, cluster);
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFBoolAndExpression expr, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression leftExpr = expr.getLeft();
		ValueType left = null, right = null;
		if (leftExpr != null) {
			left = (ValueType) leftExpr.accept(this, cluster);
		}

		SQFExpression rightExpr = expr.getRight();
		if (rightExpr != null) {
			right = (ValueType) rightExpr.accept(this, cluster);
		}

		if (left == null || right == null) {
			return ValueType.BOOLEAN;
		}

		assertIsType(left, ValueType.BOOLEAN, leftExpr);
		assertIsType(right, new ValueType[]{
						ValueType.BOOLEAN, ValueType.CODE
				}, rightExpr
		);

		return ValueType.BOOLEAN;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFBoolOrExpression expr, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression leftExpr = expr.getLeft();
		ValueType left = null, right = null;
		if (leftExpr != null) {
			left = (ValueType) leftExpr.accept(this, cluster);
		}

		SQFExpression rightExpr = expr.getRight();
		if (rightExpr != null) {
			right = (ValueType) rightExpr.accept(this, cluster);
		}

		if (left == null || right == null) {
			return ValueType.BOOLEAN;
		}

		assertIsType(left, ValueType.BOOLEAN, leftExpr);
		assertIsType(right, new ValueType[]{
						ValueType.BOOLEAN, ValueType.CODE
				}, rightExpr
		);

		return ValueType.BOOLEAN;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFBoolNotExpression expr, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression expr1 = expr.getExpr();
		if (expr1 != null) {
			ValueType type = (ValueType) expr1.accept(this, cluster);
			if (type != null) {
				assertIsType(type, ValueType.BOOLEAN, expr1);
			}
		}
		return ValueType.BOOLEAN;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFCompExpression expr, @NotNull CommandDescriptorCluster cluster) {
		if (expr.getComparisonType() != SQFCompExpression.ComparisonType.Equals) {
			binaryExprSameTypeHelper(expr, ValueType.NUMBER, cluster);
			return ValueType.BOOLEAN;
		}

		SQFExpression leftExpr = expr.getLeft();
		ValueType left = null, right = null;
		if (leftExpr != null) {
			left = (ValueType) leftExpr.accept(this, cluster);
		}

		SQFExpression rightExpr = expr.getRight();
		if (rightExpr != null) {
			right = (ValueType) rightExpr.accept(this, cluster);
		}

		if (left == null || right == null) {
			//can't be determined
			return ValueType.ANYTHING;
		}
		ValueType[] allowedTypes = {
				ValueType.NUMBER,
				ValueType.GROUP,
				ValueType.SIDE,
				ValueType.STRING,
				ValueType.OBJECT,
				ValueType.STRUCTURED_TEXT,
				ValueType.CONFIG,
				ValueType.DISPLAY,
				ValueType.CONTROL,
				ValueType.LOCATION
		};
		if (left == ValueType._VARIABLE) {
			assertIsType(right, allowedTypes, rightExpr);
			return ValueType.BOOLEAN;
		}

		for (ValueType type : allowedTypes) {
			if (left == type) {
				assertIsType(right, allowedTypes, rightExpr);
				return ValueType.BOOLEAN;
			}
		}
		notOfType(allowedTypes, left, leftExpr);
		assertIsType(right, allowedTypes, rightExpr);

		return ValueType.BOOLEAN;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFConfigFetchExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return null;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFExponentExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return null;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFLiteralExpression expr, @NotNull CommandDescriptorCluster cluster) {
		if (expr.getVar() != null) {
			return ValueType._VARIABLE;
		}
		if (expr.getArr() != null) {
			return ValueType.ARRAY;
		}
		if (expr.getStr() != null) {
			return ValueType.STRING;
		}
		if (expr.getNum() != null) {
			return ValueType.NUMBER;
		}
		throw new IllegalStateException("literal expression '" + expr.getText() + "' couldn't determine type");
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFParenExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return (ValueType) expr.getExpresssion().accept(this, cluster);
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFCommandExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return null;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFCodeBlockExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return null;
	}

	@Nullable
	@Override
	public ValueType visit(@NotNull SQFSignedExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return null;
	}

	@NotNull
	private ValueType binaryExprSameTypeHelper(@NotNull SQFBinaryExpression expr, @NotNull ValueType expected,
											   @NotNull CommandDescriptorCluster cluster) {
		SQFExpression leftExpr = expr.getLeft();
		ValueType left = null, right = null;
		if (leftExpr != null) {
			left = (ValueType) leftExpr.accept(this, cluster);
		}

		SQFExpression rightExpr = expr.getRight();
		if (rightExpr != null) {
			right = (ValueType) rightExpr.accept(this, cluster);
		}

		if (left == null || right == null) {
			//can't be determined
			return ValueType.ANYTHING;
		}

		assertIsType(left, expected, leftExpr);
		assertIsType(right, expected, rightExpr);

		return expected;
	}

	/**
	 * Reports a type error.
	 *
	 * @param expected    an array of expected {@link ValueType}. Be sure to exclude {@link ValueType#_VARIABLE}
	 * @param got         the unexpected {@link ValueType}.
	 * @param gotPsiOwner the PsiElement to which the <code>got</code> type is owner of
	 */
	private void notOfType(@NotNull ValueType[] expected, @NotNull ValueType got, @NotNull PsiElement gotPsiOwner) {
		problems.registerProblem(gotPsiOwner, "Type(s) " + Arrays.toString(expected) + " expected. Got " + got + ".", ProblemHighlightType.ERROR);
	}

	/**
	 * Checks if the given type matches one of the {@link ValueType} in expected. If they aren't equal, this method
	 * will register a problem and return false. If they are equal, this method will return true.
	 * <p>
	 * This method will automatically return true and not report an error if <code>check</code> is {@link ValueType#_VARIABLE}
	 *
	 * @param check         the {@link ValueType} to look for in <code>expected</code>
	 * @param expected      an array of expected {@link ValueType}. Be sure to exclude {@link ValueType#_VARIABLE}
	 * @param checkPsiOwner the PsiElement to which the <code>check</code> type is owner of
	 */
	private boolean assertIsType(@NotNull ValueType check, @NotNull ValueType[] expected, @NotNull PsiElement checkPsiOwner) {
		if (check == ValueType._VARIABLE) {
			return true;
		}
		for (ValueType expect : expected) {
			if (check == expect) {
				return true;
			}
		}
		problems.registerProblem(checkPsiOwner, "Type(s) " + Arrays.toString(expected) + " expected. Got " + check + ".", ProblemHighlightType.ERROR);
		return false;
	}

	/**
	 * Checks if the given type matches the expected {@link ValueType}. If they aren't equal, this method
	 * will register a problem and return false. If they are equal, this method will return true.
	 * <p>
	 * This method will automatically return true and not report an error if <code>check</code> is {@link ValueType#_VARIABLE}
	 *
	 * @param check         the {@link ValueType} to look for in <code>expected</code>
	 * @param expected      the expected {@link ValueType}. Be sure to not use {@link ValueType#_VARIABLE}
	 * @param checkPsiOwner the PsiElement to which the <code>check</code> type is owner of
	 */
	private boolean assertIsType(@NotNull ValueType check, @NotNull ValueType expected, @NotNull PsiElement checkPsiOwner) {
		if (check == ValueType._VARIABLE) {
			return true;
		}
		if (check != expected) {
			problems.registerProblem(checkPsiOwner, "Type " + expected + " expected. Got " + check + ".", ProblemHighlightType.ERROR);
			return false;
		}
		return true;
	}
}
