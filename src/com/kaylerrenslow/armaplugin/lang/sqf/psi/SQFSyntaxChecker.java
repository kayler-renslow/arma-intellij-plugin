package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Checks syntax and type for validity. This will not report problems related to grammar errors.
 * This will handle syntax errors where commands take too few or too many arguments.
 *
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
		//System.out.println(problems.getResults());
		return ret;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFScope scope, @NotNull CommandDescriptorCluster cluster) {
		List<SQFStatement> statements = scope.getChildStatements();
		ValueType ret = ValueType.NOTHING;
		for (SQFStatement statement : statements) {
			ret = (ValueType) statement.accept(this, cluster);
		}
		return ret;
	}


	@NotNull
	@Override
	public ValueType visit(@NotNull SQFAssignmentStatement statement, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression expr = statement.getExpr();
		if (expr != null) {
			expr.accept(this, cluster);
		}

		return ValueType.NOTHING;
	}

	@NotNull
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

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFExpressionStatement statement, @NotNull CommandDescriptorCluster cluster) {
		return (ValueType) statement.getExpr().accept(this, cluster);
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFQuestStatement statement, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression cond = statement.getCondition();
		if (cond != null) {
			ValueType condType = (ValueType) cond.accept(this, cluster);
			assertIsType(condType, ValueType.BOOLEAN, cond);
		}
		SQFExpression ifTrue = statement.getIfTrueExpr();
		if (ifTrue != null) {
			ifTrue.accept(this, cluster);
		}

		return ValueType.NOTHING;
	}

	@NotNull
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
			return ValueType._ERROR;
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

	@NotNull
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
			return ValueType._ERROR;
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

		return left;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFMultExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return binaryExprSameTypeHelper(expr, ValueType.NUMBER, cluster);
	}

	@NotNull
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
			return ValueType._ERROR;
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

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFModExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return binaryExprSameTypeHelper(expr, ValueType.NUMBER, cluster);
	}

	@NotNull
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

		if (right == ValueType.CODE) {
			SQFCodeBlockExpression blockExp = (SQFCodeBlockExpression) rightExpr;
			right = fullyVisitCodeBlockScope(blockExp.getBlock(), cluster);
			assertIsType(right, ValueType.BOOLEAN, blockExp);
			return ValueType.BOOLEAN;
		}

		assertIsType(right, new ValueType[]{ValueType.BOOLEAN, ValueType.CODE}, rightExpr);

		return ValueType.BOOLEAN;
	}

	@NotNull
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

		if (right == ValueType.CODE) {
			SQFCodeBlockExpression blockExp = (SQFCodeBlockExpression) rightExpr;
			right = fullyVisitCodeBlockScope(blockExp.getBlock(), cluster);
			assertIsType(right, ValueType.BOOLEAN, blockExp);
			return ValueType.BOOLEAN;
		}

		assertIsType(right, new ValueType[]{ValueType.BOOLEAN, ValueType.CODE}, rightExpr);

		return ValueType.BOOLEAN;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFBoolNotExpression expr, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression expr1 = expr.getExpr();
		if (expr1 != null) {
			ValueType type = (ValueType) expr1.accept(this, cluster);
			assertIsType(type, ValueType.BOOLEAN, expr1);
		}
		return ValueType.BOOLEAN;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFCompExpression expr, @NotNull CommandDescriptorCluster cluster) {
		if (expr.getComparisonType() != SQFCompExpression.ComparisonType.Equals
				&& expr.getComparisonType() != SQFCompExpression.ComparisonType.NotEquals) {
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
			return ValueType._ERROR;
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
				assertIsType(right, left, rightExpr);
				return ValueType.BOOLEAN;
			}
		}
		notOfType(allowedTypes, left, leftExpr);
		assertIsType(right, allowedTypes, rightExpr);

		return ValueType.BOOLEAN;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFConfigFetchExpression expr, @NotNull CommandDescriptorCluster cluster) {
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
			return ValueType.CONFIG;
		}

		assertIsType(left, ValueType.CONFIG, leftExpr);
		assertIsType(right, ValueType.STRING, rightExpr);

		return ValueType.CONFIG;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFExponentExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return binaryExprSameTypeHelper(expr, ValueType.NUMBER, cluster);
	}

	@NotNull
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

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFParenExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return (ValueType) expr.getExpr().accept(this, cluster);
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFCommandExpression expr, @NotNull CommandDescriptorCluster cluster) {
		LinkedList<CommandExpressionPart> parts = new LinkedList<>();
		SQFCommandExpression cursor = expr;
		while (true) {
			SQFCommand command = cursor.getSQFCommand();
			SQFCommandArgument pre = cursor.getPrefixArgument();
			SQFCommandArgument post = cursor.getPostfixArgument();
			if (pre != null) {
				parts.add(new CommandExpressionPart(pre));
			}
			parts.add(new CommandExpressionPart(command));
			if (post != null) {
				parts.add(new CommandExpressionPart(post));
				SQFExpression postExpr = post.getExpr();
				if (postExpr instanceof SQFCommandExpression) {
					cursor = (SQFCommandExpression) postExpr;
				} else {
					break;
				}
			} else {
				break;
			}
		}

		return getReturnTypeForCommand(parts, null, problems);
	}

	private ValueType getReturnTypeForCommand(@NotNull LinkedList<CommandExpressionPart> parts,
											  @Nullable ValueType previousCommandReturnType,
											  @NotNull ProblemsHolder problems) {

		ValueType prefixType = null, postfixType = null;

		CommandExpressionPart prefixPart = parts.removeFirst();
		CommandExpressionPart commandPart = null;
		CommandExpressionPart postfixPart = null;
		if (!prefixPart.isCommandPart()) {
			commandPart = parts.removeFirst();
			if (!commandPart.isCommandPart()) {
				throw new IllegalStateException("expected command part");
			}
		} else {
			commandPart = prefixPart;
			prefixPart = null;
		}
		CommandExpressionPart peek = parts.peekFirst();
		if (peek != null) {
			if (!peek.isCommandPart()) {
				postfixPart = parts.removeFirst();
			}
		}

		SQFCommand command = commandPart.getCommand();
		String commandName = command.getCommandName();
		CommandDescriptor descriptor;
		{ //get the descriptor
			descriptor = cluster.get(commandName);
			if (descriptor == null) {
				throw new IllegalStateException("descriptor doesn't exist for command " + commandName);
			}
			if (descriptor.getSyntaxList().size() == 0) {
				throw new IllegalStateException("command '" + commandName + "' has no syntaxes");
			}
		}

		Function<CommandExpressionPart, ValueType> getTypeForPart = commandExpressionPart -> {
			SQFCommandArgument arg = commandExpressionPart.getArgument();
			SQFExpression expr = arg.getExpr();
			SQFCodeBlock block = arg.getBlock();
			if (block == null) {
				return (ValueType) expr.accept(this, cluster);
			}
			return ValueType.CODE;
		};

		if (prefixPart != null) {
			prefixType = getTypeForPart.apply(prefixPart);
		} else {
			prefixType = previousCommandReturnType;
		}
		if (postfixPart != null) {
			postfixType = getTypeForPart.apply(postfixPart);
		}

		//syntaxes that currently work given the expression
		LinkedList<CommandSyntax> fittingSyntaxes = new LinkedList<>();

		//find syntaxes with matching prefix and postfix value types
		for (CommandSyntax syntax : descriptor.getSyntaxList()) {
			Param prefixParam = syntax.getPrefixParam();
			Param postfixParam = syntax.getPostfixParam();
			if (prefixParam == null) {
				if (prefixType != null) {
					continue;
				}
			} else {
				if (prefixType == null) {
					continue;
				}
				if (prefixType != ValueType._VARIABLE && !prefixParam.allowedTypesContains(prefixType)) {
					continue;
				}
			}
			if (postfixParam == null) {
				if (postfixType != null) {
					continue;
				}
			} else {
				if (postfixType == null) {
					continue;
				}
				if (postfixType != ValueType._VARIABLE && !postfixParam.allowedTypesContains(postfixType)) {
					continue;
				}
			}
			fittingSyntaxes.add(syntax);
		}

		if (fittingSyntaxes.isEmpty()) {
			problems.registerProblem(
					command,
					"No syntax for " +
							(prefixType == null ? "" : prefixType.getDisplayName() + " ")
							+ commandName +
							(postfixType == null ? "" : "" + postfixType.getDisplayName())
			);
			return ValueType._ERROR;
		}

		ValueType retType = ValueType._ERROR;
		if (fittingSyntaxes.size() == 1) {
			retType = fittingSyntaxes.getFirst().getReturnValue().getType();
			if (!parts.isEmpty()) {
				problems.registerProblem(parts.getFirst().getPsiElement(), "Expected ;");
			}
			return retType;
		}

		//todo check remaining syntaxes and check argument by argument


		return retType;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFCodeBlockExpression expr, @NotNull CommandDescriptorCluster cluster) {
		SQFLocalScope scope = expr.getBlock().getScope();
		if (scope != null) {
			scope.accept(this, cluster);
		}
		//we return Code because it is a literal, like 1 or 10. When commands take them as arguments,
		// they are responsible for executing the code and getting the returned value.
		return ValueType.CODE;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFSignedExpression expr, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression expr1 = expr.getExpr();
		ValueType type = (ValueType) expr1.accept(this, cluster);
		switch (expr.getSign()) {
			case Plus: {
				if (type.isArray()) {
					return ValueType.ARRAY;
				}
				if (type == ValueType.NUMBER) {
					return ValueType.NUMBER;
				}
				assertIsType(type, new ValueType[]{ValueType.NUMBER, ValueType.ARRAY}, expr1);
				return ValueType._ERROR;
			}
			case Minus: {
				assertIsType(type, ValueType.NUMBER, expr1);
				return ValueType.NUMBER;
			}
			default: {
				throw new IllegalStateException("unhandled sign:" + expr.getSign());
			}
		}
	}

	/**
	 * Fully visit the block and return the last {@link SQFStatement} type
	 *
	 * @param block   block to fully visit
	 * @param cluster cluster
	 * @return the type returned from the last {@link SQFStatement},
	 * or {@link ValueType#NOTHING} if there was no statements
	 */
	@NotNull
	private ValueType fullyVisitCodeBlockScope(@NotNull SQFCodeBlock block, @NotNull CommandDescriptorCluster cluster) {
		SQFLocalScope scope = block.getScope();
		if (scope != null) {
			return (ValueType) scope.accept(this, cluster);
		}
		return ValueType.NOTHING;
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
			return ValueType._ERROR;
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

	private static class CommandExpressionPart {
		@Nullable
		private final SQFCommandArgument argument;
		@Nullable
		private final SQFCommand command;

		public CommandExpressionPart(@NotNull SQFCommand command) {
			this.command = command;
			this.argument = null;
		}

		public CommandExpressionPart(@NotNull SQFCommandArgument argument) {
			this.command = null;
			this.argument = argument;
		}

		public boolean isCommandPart() {
			return command != null;
		}

		public boolean isArgumentPart() {
			return argument != null;
		}

		@NotNull
		public SQFCommand getCommand() {
			if (!isCommandPart()) {
				throw new IllegalStateException("can't get command on a non-command part");
			}
			return command;
		}

		@NotNull
		public SQFCommandArgument getArgument() {
			if (!isArgumentPart()) {
				throw new IllegalStateException("can't get command on a non-argument part");
			}
			return argument;
		}

		@NotNull
		public PsiElement getPsiElement() {
			return isArgumentPart() ? argument : command;
		}
	}

}
