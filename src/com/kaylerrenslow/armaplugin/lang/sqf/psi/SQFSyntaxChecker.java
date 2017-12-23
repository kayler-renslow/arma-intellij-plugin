package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
		LinkedList<PotentialProblem> potentialProblems = new LinkedList<>();
		Counter reportCounter = new Counter(0);

		LinkedList<ExprPart> parts = new LinkedList<>();
		SQFCommandExpression cursor = expr;
		while (true) {
			SQFExpressionOperator op = cursor.getExprOperator();
			SQFCommandArgument pre = cursor.getPrefixArgument();
			SQFCommandArgument post = cursor.getPostfixArgument();
			if (pre != null) {
				parts.add(new ExprPart(new CommandArgumentPart(pre)));
			}
			parts.add(new ExprPart(op));
			if (post != null) {
				SQFExpression postExpr = post.getExpr();
				if (postExpr instanceof SQFCommandExpression) {
					cursor = (SQFCommandExpression) postExpr;
				} else {
					parts.add(new ExprPart(new CommandArgumentPart(post)));
					break;
				}
			} else {
				break;
			}
		}

		LinkedList<ExprPart> groupedParts = groupTheParts(potentialProblems, reportCounter, parts);

		return getReturnTypeForCommand(
				groupedParts,
				null,
				problems,
				potentialProblems,
				reportCounter,
				false
		);
	}

	/**
	 * This method is for grouping parts on the right hand side of forward looking commands ({@link #isForwardLookingCommand(SQFExpressionOperator)}).
	 * This is done by adding everything left of the forward looking command as well as the command itself to a list
	 * and then grouping everything after it on the right. The grouping makes the right hand side evaluate like
	 * the right hand side was enclosed in parenthesis. For example, "(_i + 1) >= count _numArr || (_i + 1) >= count _opNumArr" is evaluated like
	 * "((_i + 1) >= count _numArr) || ((_i + 1) >= count _opNumArr)"
	 *
	 * @param potentialProblems a list that will be shared across all fake groups.
	 * @param reportCounter     report counter that will be shared across all fake groups
	 * @param parts             parts to make a group of
	 * @return the grouped parts, or will just return parts if there is 1 or less forward looking operators
	 */
	@NotNull
	private LinkedList<ExprPart> groupTheParts(@NotNull LinkedList<PotentialProblem> potentialProblems,
											   @NotNull Counter reportCounter, @NotNull LinkedList<ExprPart> parts) {
		final int initialPartsSize = parts.size();
		LinkedList<ExprPart> groupedParts = new LinkedList<>();
		ArrayList<Integer> forwardLookingOpIndices = new ArrayList<>();
		{
			int i = 0;
			for (ExprPart part : parts) {
				if (part.isOperatorPart() && isForwardLookingCommand(part.getOperator())) {
					forwardLookingOpIndices.add(i);
				}
				i++;
			}
		}

		if (forwardLookingOpIndices.size() <= 1) {
			return parts;
		}
		LinkedList<ExprPart> currentGroup = new LinkedList<>();
		final int STATE_START_BINARY = 0;
		final int STATE_FINISHED_BINARY = 1;
		int state = STATE_START_BINARY;
		int partsIndex = 0;
		int endIndexCount = 0;
		for (int forwardLookingOpIndex : forwardLookingOpIndices) {
			switch (state) {
				case STATE_START_BINARY: {
					for (; partsIndex < forwardLookingOpIndex; partsIndex++) {
						currentGroup.add(parts.removeFirst());
					}

					ExprPart operatorPart = parts.removeFirst();
					currentGroup.add(operatorPart);
					partsIndex++;

					final int endGroupIndex = endIndexCount + 1 >= forwardLookingOpIndices.size() ? initialPartsSize : forwardLookingOpIndices.get(endIndexCount + 1);

					for (; partsIndex < endGroupIndex; partsIndex++) {
						currentGroup.add(parts.removeFirst());
					}

					ExprPart part = new ExprPart(new ExprPartsGroupArgument(currentGroup, problems, potentialProblems, reportCounter));
					groupedParts.add(part);
					state = STATE_FINISHED_BINARY;
					currentGroup = new LinkedList<>();
					break;
				}
				case STATE_FINISHED_BINARY: {
					groupedParts.add(parts.removeFirst());
					partsIndex++;
					state = STATE_START_BINARY;
					break;
				}
			}
			endIndexCount++;
		}
		return groupedParts;
	}

	/**
	 * Gets a returned {@link ValueType} for a {@link SQFCommandExpression} that is broken up into a list of {@link ExprPart} instances.
	 * <p>
	 * This method recursively calls itself to syntax and type check command expressions. This method will also
	 * report problems to the specified {@link ProblemsHolder}. Since some commands require a right type (postfix type)
	 * and some commands can be chained (for example, "hint format ['']" is a chain of commands), the right type
	 * may be determined by recursively calling this method without error reporting (this is called peeking).
	 * <p>
	 * An example of peeking is with "hint format ['']". The first command is hint, but its right type (postfix type)
	 * requires a {@link BaseType#STRING}. It checks the next part, which is the format command. It then
	 * invokes this method with format being the starting command part. Then, format notices that the next type
	 * is not a command. If you look at the SQF grammar file, an array is contained in a {@link SQFLiteralExpression},
	 * so this method will call {@link SQFLiteralExpression#accept(SQFSyntaxVisitor, CommandDescriptorCluster)}
	 * to get the correct {@link ValueType}. From this point, format [''] will then return a {@link BaseType#STRING}
	 * and then pass it into hint.
	 * <p>
	 * Sometimes when peeking, there are problems, but the problems aren't guaranteed to be problems.
	 * They are potential problems (information stored in {@link PotentialProblem} instances) and
	 * are stored in a list and then are subsequently reported to the {@link ProblemsHolder} when the potential problems
	 * are actually problems. You can think of {@link PotentialProblem} as a future problem report when a peek fails.
	 * But when a peek succeeds, no {@link PotentialProblem} instances should be reported.
	 * Also, if a peek succeeds, all {@link PotentialProblem} instances created from the peek need to be removed.
	 *
	 * @param parts                     all parts
	 * @param previousCommandReturnType the returned value of a previous recursive call,
	 *                                  or null if the type hasn't been determined (peeking commands)
	 * @param problems                  problems
	 * @param potentialProblems         potential problems from peeking
	 * @param reportCount               how many problems have been reported (>= 0)
	 * @param isPeeking                 true if this invocation being called during a peek, false if it isn't
	 * @return the last command's returned {@link ValueType}
	 */
	@NotNull
	private ValueType getReturnTypeForCommand(@NotNull LinkedList<ExprPart> parts,
											  @Nullable ValueType previousCommandReturnType,
											  @NotNull ProblemsHolder problems,
											  @NotNull LinkedList<PotentialProblem> potentialProblems,
											  @NotNull Counter reportCount,
											  boolean isPeeking) {


		//All parts removed from the current invocation (requires a new list for each recursive call).
		//These parts will be added back if a peek wasn't used or a peek failed.
		LinkedList<ExprPart> removedParts = new LinkedList<>();

		ValueType prefixType = null;
		ExprPart prefixPart = parts.removeFirst();
		removedParts.push(prefixPart);
		ExprPart commandPart = null;

		if (!prefixPart.isOperatorPart()) {
			commandPart = parts.removeFirst();
			removedParts.push(commandPart);
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
			prefixType = prefixPart.getArgument().getType(this, isPeeking, cluster);
		} else {
			prefixType = previousCommandReturnType;
		}

		ExprPart peekPart = parts.peekFirst();
		ValueType peekType = null;

		//if there are more parts to consume and there is at least 1 syntax that accepts a postfix param,
		//there must be a peek
		boolean requirePeek = false;
		{
			for (CommandSyntax syntax : descriptor.getSyntaxList()) {
				if (syntax.getPostfixParam() != null) {
					requirePeek = true;
					break;
				}
			}
		}

		//find syntaxes with matching prefix and postfix value types
		CommandSyntax matchedSyntax = null;
		boolean usedPeekType = false;
		boolean keepPartsRemoved = false;
		for (CommandSyntax syntax : descriptor.getSyntaxList()) {
			Param prefixParam = syntax.getPrefixParam();
			Param postfixParam = syntax.getPostfixParam();
			if (prefixParam == null) {
				if (prefixType != null) {
					continue;
				}
			} else {
				if (prefixType == null) {
					if (!prefixParam.isOptional()) {
						continue;
					}
				} else {
					if (!prefixParam.containsType(prefixType)) {
						continue;
					}
				}
			}

			if (postfixParam == null) {
				if (!requirePeek) {
					matchedSyntax = syntax;
					usedPeekType = false;
					keepPartsRemoved = true;
					break;
				}
			} else {
				if (peekType == null) {
					peekType = getPeekType(parts, removedParts, problems, potentialProblems, reportCount);
				}
				if (peekType == null) {
					if (!postfixParam.isOptional()) {
						continue;
					}
					keepPartsRemoved = true;
					usedPeekType = false;
					matchedSyntax = syntax;
					break;
				} else {
					if (peekType.isHardEqual(_ERROR)) {
						continue;
					}
					if (!postfixParam.containsType(peekType)) {
						continue;
					}
					matchedSyntax = syntax;
					usedPeekType = true;
					keepPartsRemoved = true;
					break;
				}
			}
		}
		if (!usedPeekType && !keepPartsRemoved) {
			while (!removedParts.isEmpty()) {
				parts.push(removedParts.pop());
			}
		}

		if (matchedSyntax != null) {
			potentialProblems.clear();
			ValueType retType;

			//If either the left type or right type is variable, we must return variable.
			if ((prefixType != null && prefixType.isHardEqual(BaseType._VARIABLE)) || (peekType != null && peekType.isHardEqual(BaseType._VARIABLE))) {
				retType = BaseType._VARIABLE;
			} else {
				retType = matchedSyntax.getReturnValue().getType();
			}

			if (isPeeking) {
				return retType;
			}

			if (parts.isEmpty()) {
				if (peekType != null && !usedPeekType) {
					if (reportCount.count <= 0) {
						problems.registerProblem(peekPart.getPsiElement(),
								"Expected ;",
								ProblemHighlightType.GENERIC_ERROR_OR_WARNING
						);
						reportCount.count++;
					}
				}
				return retType;
			}

			boolean expectedSemicolon = false;
			boolean consumeMoreCommands = false;
			ExprPart peekFirst = parts.peekFirst();

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
				if (reportCount.count <= 0) {
					problems.registerProblem(
							parts.getFirst().getPsiElement(), "Expected ;",
							ProblemHighlightType.GENERIC_ERROR_OR_WARNING
					);
					reportCount.count++;
				}
			}
			if (consumeMoreCommands) {
				retType = getReturnTypeForCommand(parts, retType, problems, potentialProblems, reportCount, false);
			}

			return retType;
		} else {
			PotentialProblem problem;

			if (peekType == null) {
				if (prefixType == null) {
					problem = new PotentialProblem(
							exprOperator,
							"No syntax for '" + commandName + "' when left parameter is absent.",
							ProblemHighlightType.GENERIC_ERROR_OR_WARNING
					);
				} else {
					if (requirePeek) {
						problem = new PotentialProblem(
								exprOperator,
								"No syntax for '" + prefixType.getDisplayName() + " " + commandName + "'",
								ProblemHighlightType.GENERIC_ERROR_OR_WARNING
						);
					} else {
						problem = new PotentialProblem(
								exprOperator,
								"No syntax for '" + commandName + "' where " + prefixType.getDisplayName() + " is left parameter.",
								ProblemHighlightType.GENERIC_ERROR_OR_WARNING
						);
					}
				}
			} else {
				problem = new PotentialProblem(
						exprOperator,
						"No syntax for '" +
								(prefixType == null ? "" : prefixType.getDisplayName() + " ")
								+ commandName + " " + peekType.getDisplayName() + "'",
						ProblemHighlightType.GENERIC_ERROR_OR_WARNING
				);
			}

			if (reportCount.count <= 0) {
				if (!isPeeking) {
					if (potentialProblems.isEmpty()) {
						reportCount.count++;
						problems.registerProblem(problem.getErrorElement(), problem.getMessage(), problem.getHighlightType());
					} else {
						//report only the oldest potential problem
						PotentialProblem actualProblem = potentialProblems.getFirst();
						problems.registerProblem(actualProblem.getErrorElement(), actualProblem.getMessage(), actualProblem.getHighlightType());
						reportCount.count++;
						potentialProblems.clear();
					}
				} else {
					potentialProblems.add(problem);
				}
			}

			return BaseType._ERROR;
		}

	}

	@Nullable
	private ValueType getPeekType(@NotNull LinkedList<ExprPart> parts,
								  @NotNull LinkedList<ExprPart> removedParts,
								  @NotNull ProblemsHolder problems,
								  @NotNull LinkedList<PotentialProblem> potentialProblems,
								  @NotNull Counter reportCount) {
		ExprPart peekPart = parts.peekFirst();
		if (peekPart == null) {
			return null;
		}
		if (peekPart.isOperatorPart()) {
			return getReturnTypeForCommand(
					parts,
					null,
					problems,
					potentialProblems,
					reportCount,
					true
			);
		} else {
			removedParts.push(parts.removeFirst());
			return peekPart.getArgument().getType(this, true, cluster);
		}
	}

	/**
	 * For some reason, in SQF, &&, ||, ==, !=, <, >, <=, >= don't consume
	 * just the next token, but rather, evaluates everything after the operator
	 * and then uses that evaluated type as the right hand side.
	 * For example, instead of true || 1 + count [] >= 0 throwing an error saying "true || 1" is invalid,
	 * it evaluates 1 + count [] to a number, passes it into >= left and side, and the boolean created from
	 * >= is passed into || operator.
	 * <p>
	 * Another example of forward looking is with "2 < 1 + count []" will return false
	 * without errors and "2 < 1 + count [1,1,1]" will return true without errors.
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
	@Override
	public ValueType visit(@NotNull SQFCodeBlockExpression expr, @NotNull CommandDescriptorCluster cluster) {
		return new CodeType(fullyVisitCodeBlockScope(this, expr.getBlock(), cluster));
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
	private static ValueType fullyVisitCodeBlockScope(@NotNull SQFSyntaxVisitor visitor, @NotNull SQFCodeBlock block, @NotNull CommandDescriptorCluster cluster) {
		SQFLocalScope scope = block.getScope();
		if (scope != null) {
			return (ValueType) scope.accept(visitor, cluster);
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

	private interface ArgumentPart {
		@NotNull
		PsiElement getPsiElement();

		@NotNull
		ValueType getType(@NotNull SQFSyntaxChecker checker, boolean isPeeking, @NotNull CommandDescriptorCluster cluster);
	}

	private static class CommandArgumentPart implements ArgumentPart {

		@NotNull
		private final SQFCommandArgument argument;

		public CommandArgumentPart(@NotNull SQFCommandArgument argument) {
			this.argument = argument;
		}

		@NotNull
		@Override
		public PsiElement getPsiElement() {
			return argument;
		}

		@NotNull
		public SQFCommandArgument getArgument() {
			return argument;
		}

		@NotNull
		@Override
		public ValueType getType(@NotNull SQFSyntaxChecker checker, boolean isPeeking, @NotNull CommandDescriptorCluster cluster) {
			SQFExpression expr = argument.getExpr();
			SQFCodeBlock block = argument.getBlock();
			if (block == null) {
				return (ValueType) expr.accept(checker, cluster);
			}
			return new CodeType(fullyVisitCodeBlockScope(checker, block, cluster));
		}

		@Override
		public String toString() {
			return argument.getText();
		}
	}

	private static class ExprPartsGroupArgument implements ArgumentPart {
		@NotNull
		private final LinkedList<ExprPart> parts;
		@NotNull
		private final ProblemsHolder problems;
		@NotNull
		private final LinkedList<PotentialProblem> potentialProblems;
		@NotNull
		private final Counter reportCounter;

		public ExprPartsGroupArgument(@NotNull LinkedList<ExprPart> parts, @NotNull ProblemsHolder problems,
									  @NotNull LinkedList<PotentialProblem> potentialProblems,
									  @NotNull Counter reportCounter) {
			this.reportCounter = reportCounter;
			if (parts.isEmpty()) {
				throw new IllegalStateException("parts is empty");
			}
			this.parts = parts;
			this.problems = problems;
			this.potentialProblems = potentialProblems;
		}

		@NotNull
		public List<ExprPart> getParts() {
			return parts;
		}

		@NotNull
		@Override
		public PsiElement getPsiElement() {
			return parts.get(0).getPsiElement();
		}

		@NotNull
		@Override
		public ValueType getType(@NotNull SQFSyntaxChecker checker, boolean isPeeking, @NotNull CommandDescriptorCluster cluster) {
			if (parts.size() == 1 && parts.getFirst().isArgumentPart()) {
				return parts.getFirst().getArgument().getType(checker, isPeeking, cluster);
			}
			return checker.getReturnTypeForCommand(parts, null, problems, potentialProblems, reportCounter, isPeeking);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (ExprPart part : parts) {
				sb.append(part.toString());
				if (part != parts.getLast()) {
					sb.append(' ');
				}
			}
			return "```" + sb.toString() + "```";
		}
	}

	private static class ExprPart {
		@Nullable
		private final ArgumentPart argument;
		@Nullable
		private final SQFExpressionOperator operator;

		public ExprPart(@NotNull SQFExpressionOperator operator) {
			this.operator = operator;
			this.argument = null;
		}

		public ExprPart(@NotNull ArgumentPart argument) {
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
			if (operator == null) {
				throw new IllegalStateException("can't get operator on a non-operator part");
			}
			return operator;
		}

		@NotNull
		public ArgumentPart getArgument() {
			if (argument == null) {
				throw new IllegalStateException("can't get command on a non-argument part");
			}
			return argument;
		}

		@NotNull
		public PsiElement getPsiElement() {
			PsiElement psiElement = argument != null ? argument.getPsiElement() : operator;
			if (psiElement == null) {
				throw new IllegalStateException("arguemnt and operator are both null");
			}
			return psiElement;
		}

		@Override
		public String toString() {
			return operator != null ? operator.getText() : argument.toString();
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

	private static class PotentialProblem {
		@NotNull
		private final PsiElement errorElement;
		@NotNull
		private final String message;
		@NotNull
		private final ProblemHighlightType highlightType;

		public PotentialProblem(@NotNull PsiElement errorElement, @NotNull String message, @NotNull ProblemHighlightType highlightType) {
			this.errorElement = errorElement;
			this.message = message;
			this.highlightType = highlightType;
		}

		@NotNull
		public PsiElement getErrorElement() {
			return errorElement;
		}

		@NotNull
		public String getMessage() {
			return message;
		}

		@NotNull
		public ProblemHighlightType getHighlightType() {
			return highlightType;
		}
	}
}
