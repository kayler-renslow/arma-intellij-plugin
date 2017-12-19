package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

import static com.kaylerrenslow.armaplugin.lang.sqf.syntax.ValueType.BaseType.*;

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
	 * If there was no statements to check, will return {@link BaseType#NOTHING}
	 */
	@Nullable
	public ValueType begin() {
		ValueType ret = BaseType.NOTHING;
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
		ValueType ret = ValueType.BaseType.NOTHING;
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

		return BaseType.NOTHING;
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

		return BaseType.NOTHING;
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
			assertIsType(condType, BaseType.BOOLEAN, cond);
		}
		SQFExpression ifTrue = statement.getIfTrueExpr();
		if (ifTrue != null) {
			ifTrue.accept(this, cluster);
		}

		return BaseType.NOTHING;
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
			return BaseType.STRING;
		}
		if (expr.getNum() != null) {
			return BaseType.NUMBER;
		}
		if (expr.getMacroCall_() != null) {
			return BaseType._VARIABLE;
		}
		throw new IllegalStateException("literal expression '" + expr.getText() + "' couldn't determine type");
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFParenExpression expr, @NotNull CommandDescriptorCluster cluster) {
		SQFExpression expr1 = expr.getExpr();
		if (expr1 == null) {
			return _ERROR;
		}
		return (ValueType) expr1.accept(this, cluster);
	}


	@NotNull
	@Override
	public ValueType visit(@NotNull SQFCommandExpression expr, @NotNull CommandDescriptorCluster cluster) {
		LinkedList<CommandExpressionPart> parts = new LinkedList<>();
		SQFCommandExpression cursor = expr;
		while (true) {
			SQFExpressionOperator op = cursor.getExprOperator();
			SQFCommandArgument pre = cursor.getPrefixArgument();
			SQFCommandArgument post = cursor.getPostfixArgument();
			if (pre != null) {
				parts.add(new CommandExpressionPart(pre));
			}
			parts.add(new CommandExpressionPart(op));
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

		return getReturnTypeForCommand(parts, null, new Counter(0), problems);
	}

	@NotNull
	private ValueType getReturnTypeForCommand(@NotNull LinkedList<CommandExpressionPart> parts,
											  @Nullable ValueType previousCommandReturnType,
											  @NotNull Counter peekPartDepth,
											  @Nullable ProblemsHolder problems) {

		ValueType prefixType = null;

		CommandExpressionPart prefixPart = parts.removeFirst();
		CommandExpressionPart commandPart = null;
		if (!prefixPart.isOperatorPart()) {
			commandPart = parts.removeFirst();
			if (!commandPart.isOperatorPart()) {
				throw new IllegalStateException("expected command part");
			}
		} else {
			commandPart = prefixPart;
			prefixPart = null;
		}

		SQFExpressionOperator exprOperator = commandPart.getOperator();
		CommandDescriptor descriptor = getDescriptorForOperator(exprOperator, cluster);

		String commandName = descriptor.getCommandName();

		if (prefixPart != null) {
			prefixType = getValueTypeForPart(prefixPart);
		} else {
			prefixType = previousCommandReturnType;
		}

		LinkedList<CommandExpressionPart> partsAfterGettingPeekNextPartType = new LinkedList<>();
		partsAfterGettingPeekNextPartType.addAll(parts);
		int peekDepthBeforeGettingPeekNextPartType = peekPartDepth.count;
		ValueType peekNextPartType = null;
		{
			CommandExpressionPart peekNextPart = parts.peekFirst();

			if (peekNextPart != null) {

				if (peekNextPart.isOperatorPart()) {
					if (isForwardLookingCommand(peekNextPart.getOperator())) {
						//forward looking commands are binary expressions, so there is no point in peeking ahead.
						peekNextPartType = null;
					} else {
						//pass null to problems reporter because we are TESTING to see if
						//the peeked next part COULD work. If there was problems, we can just ignore it.
						peekNextPartType = getReturnTypeForCommand(
								partsAfterGettingPeekNextPartType,
								null,
								peekPartDepth,
								null
						);
					}
				} else {
					final boolean isForwardLookingCommand = isForwardLookingCommand(commandPart.getOperator());
					if (isForwardLookingCommand && parts.size() > 1) {
						peekNextPartType = getReturnTypeForCommand(
								partsAfterGettingPeekNextPartType,
								null,
								peekPartDepth,
								null
						);
					} else {
						peekNextPartType = getValueTypeForPart(peekNextPart);
						partsAfterGettingPeekNextPartType.removeFirst();
					}
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
				if (!prefixParam.isOptional()) {
					if (prefixType == null) {
						continue;
					}
				}
				if (prefixType != null && !prefixParam.containsType(prefixType)) {
					continue;
				}
			}
			if (postfixParam == null) {
				usingPeekedNextPart = false;
			} else {
				if (peekNextPartType != null && peekNextPartType.isHardEqual(_ERROR)) {
					continue;
				}
				if (!postfixParam.isOptional()) {
					if (peekNextPartType == null) {
						continue;
					}
				}
				if (peekNextPartType != null && !postfixParam.containsType(peekNextPartType)) {
					continue;
				}
				usingPeekedNextPart = true;
			}

			if (usingPeekedNextPart) {
				//since parts is duplicated for each recursive call, we need to make sure
				//we consume the same amount of parts as the previous recursive call
				//for cases like parseText localize ''
				for (int i = 0; !partsAfterGettingPeekNextPartType.isEmpty() && i < peekPartDepth.count; i++) {
					partsAfterGettingPeekNextPartType.removeFirst();
				}
				peekPartDepth.count++;
				parts = partsAfterGettingPeekNextPartType;
			} else {
				peekPartDepth.count = peekDepthBeforeGettingPeekNextPartType;
			}

			ValueType retType = syntax.getReturnValue().getType();
			if (!parts.isEmpty()) {
				CommandExpressionPart peekFirst = parts.peekFirst();
				boolean expectedSemicolon = false;
				boolean consumeMoreCommands = false;
				if (peekFirst.isOperatorPart()) {
					SQFExpressionOperator peekExprOperator = peekFirst.getOperator();
					CommandDescriptor d = getDescriptorForOperator(peekExprOperator, cluster);
					for (CommandSyntax syntax1 : d.getSyntaxList()) {
						if (syntax1.getPrefixParam() != null) {
							if (syntax1.getPrefixParam().containsType(retType)) {
								expectedSemicolon = false;
								consumeMoreCommands = true;
								break;
							} else {
								expectedSemicolon = true;
							}
						} else {
							expectedSemicolon = true;
						}
					}
				} else {
					expectedSemicolon = true;
				}
				if (expectedSemicolon) {
					if (problems != null) {
						problems.registerProblem(
								parts.getFirst().getPsiElement(), "Expected ;",
								ProblemHighlightType.GENERIC_ERROR_OR_WARNING
						);
					}
				}
				if (consumeMoreCommands) {
					peekPartDepth.count = 0; //reset depth
					retType = getReturnTypeForCommand(parts, retType, peekPartDepth, problems);
				}
			}
			return retType;
		}
		if (problems != null) {
			if (peekNextPartType == null) {
				problems.registerProblem(
						exprOperator,
						"No syntax for '" +
								(prefixType == null ? "" : prefixType.getDisplayName() + " ")
								+ commandName + "'",
						ProblemHighlightType.GENERIC_ERROR_OR_WARNING
				);
			} else {
				problems.registerProblem(
						exprOperator,
						"No syntax for '" +
								(prefixType == null ? "" : prefixType.getDisplayName() + " ")
								+ commandName + " " + peekNextPartType.getDisplayName() + "'",
						ProblemHighlightType.GENERIC_ERROR_OR_WARNING
				);
			}
		}
		return BaseType._ERROR;
	}

	/**
	 * For some reason, in SQF, &&, ||, ==, !=, <, >, <=, >= don't consume
	 * just the next token, but rather, evaluates everything after the operator
	 * and then uses that evaluated type as the right hand side.
	 * For example, instead of true || 1 + count [] >= 0 throwing an error saying "true || 1" is invalid,
	 * it evaluates 1 + count [] to a number, passes it into >= left and side, and the boolean created from
	 * >= is passed into || operator.
	 * <p>
	 * This forward looking behavior also is present for the left operand. not isServer && !isNull player
	 * will evaluate "not isServer" completely, then "!isNull player", then combine both results in &&.
	 * <p>
	 * isForwardLooking is a simple check to see if the command is one of the operators that behaves this way.
	 */
	private boolean isForwardLookingCommand(@NotNull SQFExpressionOperator operator) {
		IElementType operatorType = operator.getOperatorType();
		return operatorType == SQFTypes.BARBAR
				|| operatorType == SQFTypes.AMPAMP
				|| operatorType == SQFTypes.EQEQ
				|| operatorType == SQFTypes.NE
				|| operatorType == SQFTypes.GT
				|| operatorType == SQFTypes.GE
				|| operatorType == SQFTypes.LT
				|| operatorType == SQFTypes.LE;
	}

	@NotNull
	private ValueType getValueTypeForPart(CommandExpressionPart commandExpressionPart) {
		SQFCommandArgument arg = commandExpressionPart.getArgument();
		SQFExpression expr = arg.getExpr();
		SQFCodeBlock block = arg.getBlock();
		if (block == null) {
			return (ValueType) expr.accept(this, cluster);
		}
		return new CodeType(fullyVisitCodeBlockScope(block, cluster));
	}

	@NotNull
	@Override
	public ValueType visit(@NotNull SQFCodeBlockExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return new CodeType(fullyVisitCodeBlockScope(expr.getBlock(), cluster));
	}

	/**
	 * Fully visit the block and return the last {@link SQFStatement} type
	 *
	 * @param block   block to fully visit
	 * @param cluster cluster
	 * @return the type returned from the last {@link SQFStatement},
	 * or {@link BaseType#NOTHING} if there was no statements
	 */
	@NotNull
	private ValueType fullyVisitCodeBlockScope(@NotNull SQFCodeBlock block, @NotNull CommandDescriptorCluster cluster) {
		SQFLocalScope scope = block.getScope();
		if (scope != null) {
			return (ValueType) scope.accept(this, cluster);
		}
		return ValueType.BaseType.NOTHING;
	}

	@NotNull
	public CommandDescriptor getDescriptorForOperator(@NotNull SQFExpressionOperator operator, @NotNull CommandDescriptorCluster cluster) {
		CommandDescriptor descriptor;
		SQFCommand command = operator.getCmd();
		if (command != null) {
			String commandName = command.getCommandName();
			descriptor = cluster.get(commandName);
			if (descriptor == null) {
				throw new IllegalStateException("descriptor doesn't exist for command " + commandName);
			}
		} else {
			IElementType operatorType = operator.getOperatorType();
			descriptor = ArithOperatorCommandDescriptors.get(operatorType);
			if (descriptor == null) {
				throw new IllegalStateException("operator " + operatorType + " doesn't have a command descriptor");
			}
		}
		if (descriptor.getSyntaxList().size() == 0) {
			throw new IllegalStateException("CommandDescriptor '" + descriptor.getCommandName() + "' has no syntaxes");
		}

		return descriptor;
	}

	/**
	 * Reports a type error.
	 *
	 * @param expected    an array of expected {@link ValueType}. Be sure to exclude {@link BaseType#_VARIABLE}
	 * @param got         the unexpected {@link ValueType}.
	 * @param gotPsiOwner the PsiElement to which the <code>got</code> type is owner of
	 */
	private void notOfType(@NotNull ValueType[] expected, @NotNull ValueType got, @NotNull PsiElement gotPsiOwner) {
		String expectedTypes = getExpectedArrayAsString(expected);
		problems.registerProblem(gotPsiOwner, "Type(s) " + expectedTypes + " expected. Got "
				+ got.getDisplayName() + ".", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
	}

	/**
	 * Checks if the given type matches one of the {@link ValueType} in expected. If they aren't equal, this method
	 * will register a problem and return false. If they are equal, this method will return true.
	 * <p>
	 * If check is hardEqual ({@link ValueType#isHardEqual(ValueType)}) to {@link BaseType#_VARIABLE}, this method will
	 * return immediately without reporting errors.
	 * <p>
	 * This method simply checks by via {@link ValueType#typeEquivalent(ValueType, ValueType)}.
	 *
	 * @param check         the {@link ValueType} to look for in <code>expected</code>
	 * @param expected      an array of expected {@link ValueType}. Be sure to exclude {@link BaseType#_VARIABLE},
	 *                      otherwise it will show up in the error message.
	 * @param checkPsiOwner the PsiElement to which the <code>check</code> type is owner of
	 */
	private void assertIsType(@NotNull ValueType check, @NotNull ValueType[] expected, @NotNull PsiElement checkPsiOwner) {
		if (check.isHardEqual(_VARIABLE)) {
			return;
		}
		for (ValueType expect : expected) {
			if (ValueType.typeEquivalent(expect, check)) {
				return;
			}
		}
		String expectedTypes = getExpectedArrayAsString(expected);
		problems.registerProblem(checkPsiOwner, expectedTypes + " expected. Got "
				+ check.getDisplayName() + ".", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
	}

	@NotNull
	private String getExpectedArrayAsString(@NotNull ValueType[] expected) {
		StringBuilder expectedTypes = new StringBuilder();
		int i = 0;
		for (ValueType t : expected) {
			expectedTypes.append(t.getDisplayName());
			if (i < expected.length - 1) {
				if (i == expected.length - 2) {
					expectedTypes.append(", or ");
				} else {
					expectedTypes.append(", ");
				}
			}
			i++;
		}
		return expectedTypes.toString();
	}

	/**
	 * Checks if the given type matches the expected {@link ValueType}. If they aren't equal, this method
	 * will register a problem and return false. If they are equal, this method will return true.
	 * <p>
	 * If check is hardEqual ({@link ValueType#isHardEqual(ValueType)}) to {@link BaseType#_VARIABLE}, this method will
	 * return immediately without reporting errors.
	 * <p>
	 * This method simply checks by via {@link ValueType#typeEquivalent(ValueType, ValueType)}.
	 *
	 * @param check         the {@link ValueType} to look for in <code>expected</code>
	 * @param expected      the expected {@link ValueType}. Be sure to not use {@link BaseType#_VARIABLE}
	 * @param checkPsiOwner the PsiElement to which the <code>check</code> type is owner of
	 */
	private void assertIsType(@NotNull ValueType check, @NotNull ValueType expected, @NotNull PsiElement checkPsiOwner) {
		if (check.isHardEqual(_VARIABLE)) {
			return;
		}
		if (ValueType.typeEquivalent(expected, check)) {
			return;
		}
		problems.registerProblem(checkPsiOwner, "Type " + expected.getDisplayName() + " expected. Got "
				+ check.getDisplayName() + ".", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
	}

	private static class CommandExpressionPart {
		@Nullable
		private final SQFCommandArgument argument;
		@Nullable
		private final SQFExpressionOperator operator;

		public CommandExpressionPart(@NotNull SQFExpressionOperator operator) {
			this.operator = operator;
			this.argument = null;
		}

		public CommandExpressionPart(@NotNull SQFCommandArgument argument) {
			this.operator = null;
			this.argument = argument;
		}

		public boolean isOperatorPart() {
			return operator != null;
		}

		public boolean isArgumentPart() {
			return argument != null;
		}

		@NotNull
		public SQFExpressionOperator getOperator() {
			if (!isOperatorPart()) {
				throw new IllegalStateException("can't get operator on a non-operator part");
			}
			return operator;
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
			return isArgumentPart() ? argument : operator;
		}

		@Override
		public String toString() {
			return getPsiElement().getText();
		}
	}

	private static class Counter {
		private int count = 0;

		public Counter(int i) {
			this.count = i;
		}

		@Override
		public String toString() {
			return count + "";
		}
	}

}
