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

import static com.kaylerrenslow.armaplugin.lang.sqf.syntax.ValueType.Lookup.*;

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
	 * If there was no statements to check, will return {@link ValueType.Lookup#NOTHING}
	 */
	@Nullable
	public ValueType begin() {
		ValueType ret = ValueType.Lookup.NOTHING;
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
		ValueType ret = ValueType.Lookup.NOTHING;
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

		return ValueType.Lookup.NOTHING;
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

		return ValueType.Lookup.NOTHING;
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
			assertIsType(condType, ValueType.Lookup.BOOLEAN, cond);
		}
		SQFExpression ifTrue = statement.getIfTrueExpr();
		if (ifTrue != null) {
			ifTrue.accept(this, cluster);
		}

		return ValueType.Lookup.NOTHING;
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
			return ValueType.Lookup._ERROR;
		}

		ValueType[] allowedTypes = {
				ValueType.Lookup.NUMBER,
				ValueType.Lookup.STRING,
				ValueType.Lookup.ARRAY
		};

		if (left == NUMBER || left == STRING) {
			assertIsType(right, left, rightExpr);
			return NUMBER;
		} else if (left == _VARIABLE) {
			assertIsType(right, allowedTypes, rightExpr);
			return _VARIABLE;
		}

		if (left.isArray() && right == _VARIABLE) {
			return ValueType.Lookup.ARRAY;
		}
		if (left.isArray() && right.isArray()) {
			return ValueType.Lookup.ARRAY;
		}
		notOfType(allowedTypes, right, rightExpr);
		return ValueType.Lookup.ANYTHING;
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
			return ValueType.Lookup._ERROR;
		}

		ValueType[] allowedTypes = {
				ValueType.Lookup.NUMBER,
				ValueType.Lookup.ARRAY
		};
		if (left == NUMBER) {
			assertIsType(right, ValueType.Lookup.NUMBER, rightExpr);
			return NUMBER;
		}
		if (left == _VARIABLE) {
			assertIsType(right, allowedTypes, rightExpr);
			return _VARIABLE;
		}

		if (left.isArray() && right == _VARIABLE) {
			return ValueType.Lookup.ARRAY;
		}
		if (left.isArray() && right.isArray()) {
			return ValueType.Lookup.ARRAY;
		}
		notOfType(allowedTypes, right, rightExpr);
		return ValueType.Lookup.ANYTHING;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFMultExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return binaryExprSameTypeHelper(expr, ValueType.Lookup.NUMBER, cluster);
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
			return ValueType.Lookup._ERROR;
		}

		ValueType[] allowedTypes = {
				ValueType.Lookup.NUMBER,
				ValueType.Lookup.STRING
		};

		if (left == NUMBER) {
			assertIsType(right, ValueType.Lookup.NUMBER, rightExpr);
			return ValueType.Lookup.NUMBER;
		}
		if (left == CONFIG) {
			assertIsType(right, ValueType.Lookup.STRING, rightExpr);
			return CONFIG;
		}
		if (left == _VARIABLE) {
			assertIsType(right, allowedTypes, rightExpr);
			return _VARIABLE;
		}

		notOfType(allowedTypes, right, rightExpr);
		return ValueType.Lookup.ANYTHING;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFModExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return binaryExprSameTypeHelper(expr, ValueType.Lookup.NUMBER, cluster);
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
			return ValueType.Lookup.BOOLEAN;
		}

		assertIsType(left, ValueType.Lookup.BOOLEAN, leftExpr);

		if (right == ValueType.Lookup.CODE) {
			SQFCodeBlockExpression blockExp = (SQFCodeBlockExpression) rightExpr;
			right = fullyVisitCodeBlockScope(blockExp.getBlock(), cluster);
			assertIsType(right, ValueType.Lookup.BOOLEAN, blockExp);
			return ValueType.Lookup.BOOLEAN;
		}

		assertIsType(right, new ValueType[]{ValueType.Lookup.BOOLEAN, ValueType.Lookup.CODE}, rightExpr);

		return ValueType.Lookup.BOOLEAN;
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
			return ValueType.Lookup.BOOLEAN;
		}

		assertIsType(left, ValueType.Lookup.BOOLEAN, leftExpr);

		if (right == ValueType.Lookup.CODE) {
			SQFCodeBlockExpression blockExp = (SQFCodeBlockExpression) rightExpr;
			right = fullyVisitCodeBlockScope(blockExp.getBlock(), cluster);
			assertIsType(right, ValueType.Lookup.BOOLEAN, blockExp);
			return ValueType.Lookup.BOOLEAN;
		}

		assertIsType(right, new ValueType[]{ValueType.Lookup.BOOLEAN, ValueType.Lookup.CODE}, rightExpr);

		return ValueType.Lookup.BOOLEAN;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFBoolNotExpression expr, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression expr1 = expr.getExpr();
		if (expr1 != null) {
			ValueType type = (ValueType) expr1.accept(this, cluster);
			assertIsType(type, ValueType.Lookup.BOOLEAN, expr1);
		}
		return ValueType.Lookup.BOOLEAN;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFCompExpression expr, @NotNull CommandDescriptorCluster cluster) {
		if (expr.getComparisonType() != SQFCompExpression.ComparisonType.Equals
				&& expr.getComparisonType() != SQFCompExpression.ComparisonType.NotEquals) {
			binaryExprSameTypeHelper(expr, ValueType.Lookup.NUMBER, cluster);
			return ValueType.Lookup.BOOLEAN;
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
			return ValueType.Lookup._ERROR;
		}
		ValueType[] allowedTypes = {
				ValueType.Lookup.NUMBER,
				ValueType.Lookup.GROUP,
				ValueType.Lookup.SIDE,
				ValueType.Lookup.STRING,
				ValueType.Lookup.OBJECT,
				ValueType.Lookup.STRUCTURED_TEXT,
				CONFIG,
				ValueType.Lookup.DISPLAY,
				ValueType.Lookup.CONTROL,
				ValueType.Lookup.LOCATION
		};
		if (left == _VARIABLE) {
			assertIsType(right, allowedTypes, rightExpr);
			return ValueType.Lookup.BOOLEAN;
		}

		for (ValueType type : allowedTypes) {
			if (left == type) {
				assertIsType(right, left, rightExpr);
				return ValueType.Lookup.BOOLEAN;
			}
		}
		notOfType(allowedTypes, left, leftExpr);
		assertIsType(right, allowedTypes, rightExpr);

		return ValueType.Lookup.BOOLEAN;
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
			return CONFIG;
		}

		assertIsType(left, CONFIG, leftExpr);
		assertIsType(right, ValueType.Lookup.STRING, rightExpr);

		return CONFIG;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFExponentExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return binaryExprSameTypeHelper(expr, ValueType.Lookup.NUMBER, cluster);
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFLiteralExpression expr, @NotNull CommandDescriptorCluster cluster) {
		if (expr.getVar() != null) {
			return _VARIABLE;
		}
		SQFArray arr = expr.getArr();
		if (arr != null) {
			if (arr.getExpressions().size() == 1) {
				return new SingletonArrayExpandedValueType(
						(ValueType) arr.getExpressions().get(0).accept(this, cluster)
				);
			}
			ExpandedValueType expandedValueType = new ExpandedValueType(false);
			for (SQFExpression arrItemExpr : arr.getExpressions()) {
				expandedValueType.addValueType((ValueType) arrItemExpr.accept(this, cluster));
			}
			return expandedValueType;
		}
		if (expr.getStr() != null) {
			return ValueType.Lookup.STRING;
		}
		if (expr.getNum() != null) {
			return ValueType.Lookup.NUMBER;
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
				SQFExpression postExpr = post.getExpr();
				if (postExpr instanceof SQFCommandExpression) {
					cursor = (SQFCommandExpression) postExpr;
				} else {
					parts.add(new CommandExpressionPart(post));
					break;
				}
			} else {
				break;
			}
		}

		return getReturnTypeForCommand(parts, null, problems);
	}

	@NotNull
	private ValueType getReturnTypeForCommand(@NotNull LinkedList<CommandExpressionPart> parts,
											  @Nullable ValueType previousCommandReturnType,
											  @Nullable ProblemsHolder problems) {

		ValueType prefixType = null;

		CommandExpressionPart prefixPart = parts.removeFirst();
		CommandExpressionPart commandPart = null;
		if (!prefixPart.isCommandPart()) {
			commandPart = parts.removeFirst();
			if (!commandPart.isCommandPart()) {
				throw new IllegalStateException("expected command part");
			}
		} else {
			commandPart = prefixPart;
			prefixPart = null;
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
			return ValueType.Lookup.CODE;
		};

		if (prefixPart != null) {
			prefixType = getTypeForPart.apply(prefixPart);
		} else {
			prefixType = previousCommandReturnType;
		}

		LinkedList<CommandExpressionPart> partsAfterGettingPeekNextPartType;
		ValueType peekNextPartType = null;
		{
			CommandExpressionPart peekNextPart = parts.peekFirst();
			partsAfterGettingPeekNextPartType = new LinkedList<>();
			partsAfterGettingPeekNextPartType.addAll(parts);

			if (peekNextPart != null) {
				if (peekNextPart.isCommandPart()) {
					peekNextPartType = getReturnTypeForCommand(partsAfterGettingPeekNextPartType, null, null);
				} else {
					peekNextPartType = getTypeForPart.apply(peekNextPart);
					partsAfterGettingPeekNextPartType.removeFirst();
				}
			}
		}

		//find syntaxes with matching prefix and postfix value types
		boolean usingPeekedNextPart = false;
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
				if (prefixType != _VARIABLE && !prefixParam.allowedTypesContains(prefixType)) {
					continue;
				}
			}
			if (postfixParam == null) {
				usingPeekedNextPart = false;
			} else {
				if (peekNextPartType == null) {
					continue;
				}
				if (peekNextPartType == _ERROR) {
					continue;
				}
				if (peekNextPartType != _VARIABLE && !postfixParam.allowedTypesContains(peekNextPartType)) {
					continue;
				}
				usingPeekedNextPart = true;
			}

			if (usingPeekedNextPart) {
				parts = partsAfterGettingPeekNextPartType;
			}

			ValueType retType = syntax.getReturnValue().getType();
			if (!parts.isEmpty()) {
				CommandExpressionPart peekFirst = parts.peekFirst();
				boolean expectedSemicolon = false;
				boolean consumeMoreCommands = false;
				if (peekFirst.isCommandPart()) {
					SQFCommand peekCommand = peekFirst.getCommand();
					CommandDescriptor d = cluster.get(peekCommand.getCommandName());
					if (d == null) {
						throw new IllegalStateException("descriptor doesn't exist for command " + commandName);
					}
					for (CommandSyntax syntax1 : d.getSyntaxList()) {
						if (syntax1.getPrefixParam() != null) {
							if (syntax1.getPrefixParam().allowedTypesContains(retType)) {
								expectedSemicolon = false;
								consumeMoreCommands = true;
								break;
							} else {
								expectedSemicolon = true;
							}
						}
					}
				} else {
					expectedSemicolon = true;
				}
				if (expectedSemicolon) {
					if (problems != null) {
						problems.registerProblem(parts.getFirst().getPsiElement(), "Expected ;");
					}
				}
				if (consumeMoreCommands) {
					getReturnTypeForCommand(parts, retType, problems);
				}
			}
			return retType;
		}
		if (problems != null) {
			problems.registerProblem(
					command,
					"No syntax for '" +
							(prefixType == null ? "" : prefixType.getDisplayName() + " ")
							+ commandName + "'"
			);
			problems.registerProblem(
					command,
					"No syntax for '" +
							(prefixType == null ? "" : prefixType.getDisplayName() + " ")
							+ commandName +
							(peekNextPartType == null ? "" : " " + peekNextPartType.getDisplayName()) + "'"
			);
		}
		return Lookup._ERROR;
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
		return ValueType.Lookup.CODE;
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFSignedExpression expr, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression expr1 = expr.getExpr();
		ValueType type = (ValueType) expr1.accept(this, cluster);
		switch (expr.getSign()) {
			case Plus: {
				if (type.isArray()) {
					return ValueType.Lookup.ARRAY;
				}
				if (type == ValueType.Lookup.NUMBER) {
					return ValueType.Lookup.NUMBER;
				}
				assertIsType(type, new ValueType[]{ValueType.Lookup.NUMBER, ValueType.Lookup.ARRAY}, expr1);
				return ValueType.Lookup._ERROR;
			}
			case Minus: {
				assertIsType(type, ValueType.Lookup.NUMBER, expr1);
				return ValueType.Lookup.NUMBER;
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
	 * or {@link ValueType.Lookup#NOTHING} if there was no statements
	 */
	@NotNull
	private ValueType fullyVisitCodeBlockScope(@NotNull SQFCodeBlock block, @NotNull CommandDescriptorCluster cluster) {
		SQFLocalScope scope = block.getScope();
		if (scope != null) {
			return (ValueType) scope.accept(this, cluster);
		}
		return ValueType.Lookup.NOTHING;
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
			return ValueType.Lookup._ERROR;
		}

		assertIsType(left, expected, leftExpr);
		assertIsType(right, expected, rightExpr);

		return expected;
	}

	/**
	 * Reports a type error.
	 *
	 * @param expected    an array of expected {@link ValueType}. Be sure to exclude {@link ValueType.Lookup#_VARIABLE}
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
	 * This method will automatically return true and not report an error if <code>check</code> is {@link ValueType.Lookup#_VARIABLE}
	 * <p>
	 * If expected contains an array type ({@link ValueType#isArray()}), this method will return true if check is an array as well.
	 * <p>
	 * This method simply checks by {@link ValueType.Lookup} instances and not {@link ExpandedValueType}.
	 *
	 * @param check         the {@link ValueType} to look for in <code>expected</code>
	 * @param expected      an array of expected {@link ValueType}. Be sure to exclude {@link ValueType.Lookup#_VARIABLE}
	 * @param checkPsiOwner the PsiElement to which the <code>check</code> type is owner of
	 */
	private void assertIsType(@NotNull ValueType check, @NotNull ValueType[] expected, @NotNull PsiElement checkPsiOwner) {
		if (check == _VARIABLE) {
			return;
		}
		for (ValueType expect : expected) {
			if (check.isArray() && expect.isArray()) {
				return;
			}
			if (check == expect) {
				return;
			}
		}
		problems.registerProblem(checkPsiOwner, "Type(s) " + Arrays.toString(expected) + " expected. Got " + check + ".", ProblemHighlightType.ERROR);
	}

	/**
	 * Checks if the given type matches the expected {@link ValueType}. If they aren't equal, this method
	 * will register a problem and return false. If they are equal, this method will return true.
	 * <p>
	 * This method will automatically return true and not report an error if <code>check</code> is {@link ValueType.Lookup#_VARIABLE}.
	 * <p>
	 * If expected is an array, this method will return true if check is an array as well. This will not compare elements in the array!
	 * To check if a type is an array, {@link ValueType#isArray()} is used.
	 * <p>
	 * This method simply checks by {@link ValueType.Lookup} instances and not {@link ExpandedValueType}.
	 *
	 * @param check         the {@link ValueType} to look for in <code>expected</code>
	 * @param expected      the expected {@link ValueType}. Be sure to not use {@link ValueType.Lookup#_VARIABLE}
	 * @param checkPsiOwner the PsiElement to which the <code>check</code> type is owner of
	 */
	private void assertIsType(@NotNull ValueType check, @NotNull ValueType expected, @NotNull PsiElement checkPsiOwner) {
		if (check == _VARIABLE) {
			return;
		}
		if (check.isArray() && expected.isArray()) {
			return;
		}
		if (check != expected) {
			problems.registerProblem(checkPsiOwner, "Type " + expected + " expected. Got " + check + ".", ProblemHighlightType.ERROR);
		}
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

		@Override
		public String toString() {
			return getPsiElement().getText();
		}
	}

}
