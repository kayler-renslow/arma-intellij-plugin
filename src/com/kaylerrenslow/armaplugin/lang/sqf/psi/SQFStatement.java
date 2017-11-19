package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaDialogCreator.util.Reference;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public interface SQFStatement extends PsiElement, SQFSyntaxNode {

	/**
	 * Gets all private vars declared private in this statement. Note that private variables may be declared private in the
	 * statement, but not in the same scope as the statement!
	 * <p>
	 * This method will return an empty list if the statement can create private variables but none were made private.
	 *
	 * @return list of private vars
	 */
	@NotNull
	default List<SQFPrivateVar> getDeclaredPrivateVars() {
		SQFScope containingScope = SQFScope.getContainingScope(this);
		List<SQFPrivateVar> vars = new ArrayList<>();

		Function<SQFCodeBlockExpression, Void> checkCodeBlockExp = sqfCodeBlockExpression -> {
			SQFCodeBlock codeBlock = sqfCodeBlockExpression.getBlock();
			SQFLocalScope codeBlockScope = codeBlock.getScope();
			if (codeBlockScope != null) {
				//don't check containing scope because we already checked the assignment's scope, which is containing scope
				List<SQFPrivateVar> blockPrivateVars = codeBlockScope.getPrivateVarInstances(false);

				for (SQFPrivateVar blockPrivateVar : blockPrivateVars) {
					boolean varEqual = false;
					for (SQFPrivateVar statementPrivateVar : vars) {
						if (blockPrivateVar.getVariableNameObj().nameEquals(statementPrivateVar.getVariableNameObj())
								&& blockPrivateVar.getMaxScope() == statementPrivateVar.getMaxScope()) {
							//we need to check scopes as well because a variable may be declared private in a scope
							//but that may not be it's max scope
							varEqual = true;
							break;
						}
					}
					if (!varEqual) {
						vars.add(blockPrivateVar);
					}
				}
			}
			return null;
		};

		/*
		* Traverse SQFCodeBlock expressions, but not their children
		* because each call to checkCodeBlockExpr will check the code block's children
		*/
		Function<SQFExpression, Void> findAllCodeExpr = sqfExpression -> {
			PsiUtil.traverseInLayers(sqfExpression.getNode(), astNode -> {
				PsiElement nodeAsElement = astNode.getPsi();
				if (nodeAsElement instanceof SQFCodeBlockExpression) {
					SQFCodeBlockExpression codeBlockExpression = ((SQFCodeBlockExpression) nodeAsElement);
					checkCodeBlockExp.apply(codeBlockExpression);
					return true; //don't add children
				}
				return false;
			});
			return null;
		};

		if (this instanceof SQFAssignmentStatement) {
			SQFAssignmentStatement assignment = (SQFAssignmentStatement) this;
			if (assignment.isPrivate()) {
				vars.add(new SQFPrivateVar(assignment.getVar().getVarNameObj(), assignment.getVar(), containingScope));
			}
			if (assignment.getExpr() != null) {
				SQFExpression assignmentExpr = assignment.getExpr();
				findAllCodeExpr.apply(assignmentExpr);
			}
			return vars;
		}
		if (this instanceof SQFExpressionStatement) {
			SQFExpression expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
			if (expr instanceof SQFCommandExpression) {
				SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
				switch (cmdExpr.getSQFCommand().getCommandName().toLowerCase()) {
					case "private": {
						SQFCommandArgument postfixArgument = cmdExpr.getPostfixArgument();
						if (postfixArgument == null) {
							return vars;
						}

						if (postfixArgument.getExpr().withoutParenthesis() instanceof SQFLiteralExpression) {
							SQFLiteralExpression literal = (SQFLiteralExpression) postfixArgument.getExpr().withoutParenthesis();
							if (literal.getArr() != null) {
								//private ["_var"]
								for (SQFExpression arrExp : literal.getArr().getExpressions()) {
									SQFExpression arrExpNoParen = arrExp.withoutParenthesis();
									if (arrExpNoParen instanceof SQFLiteralExpression) {
										SQFLiteralExpression arrExpLiteral = (SQFLiteralExpression) arrExpNoParen;
										if (arrExpLiteral.getStr() != null) {
											SQFString str = arrExpLiteral.getStr();
											if (str.containsLocalVariable()) {
												vars.add(
														new SQFPrivateVar(
																new SQFVariableName(str.getNonQuoteText()),
																str, containingScope
														)
												);
											}
										}
									}
								}
							} else if (literal.getStr() != null) {
								//private "_var"
								if (literal.getStr().containsLocalVariable()) {
									vars.add(
											new SQFPrivateVar(
													new SQFVariableName(literal.getStr().getNonQuoteText()),
													literal.getStr(),
													containingScope
											)
									);
								}
							}
						}
						break;
					}
					case "params": { //todo
						break;
					}
					case "param": { //todo
						break;
					}
					case "spawn": { //todo
						break;
					}
				}
				return vars;
			} else {
				findAllCodeExpr.apply(expr);
			}
		}
		/*todo
		//(https://community.bistudio.com/wiki/Variables#Scope)
		SQFControlStructure controlStructure = getControlStructure();
		if (controlStructure == null) {
			return vars;
		}
		*/
		return vars;
	}

	/**
	 * @return the {@link SQFIfHelperStatement} instance if the statement contains an if statement,
	 * or null if the statement isn't a valid if statement
	 */
	@Nullable
	default SQFIfHelperStatement getIfStatement() {
		SQFExpression expr;
		if (this instanceof SQFExpressionStatement) {
			expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
		} else if (this instanceof SQFAssignmentStatement) {
			expr = ((SQFAssignmentStatement) this).getExpr();
			if (expr == null) {
				return null;
			}
			expr = expr.withoutParenthesis();
		} else {
			return null;
		}

		if (!(expr instanceof SQFCommandExpression)) {
			return null;
		}
		SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
		if (!cmdExpr.commandNameEquals("if")) {
			return null;
		}
		SQFExpression condition = null;
		SQFBlockOrExpression thenBlock = null;
		SQFBlockOrExpression elseBlock = null;

		//get condition, then block, and else block
		{
			SQFCommandArgument postArg = cmdExpr.getPostfixArgument();
			if (postArg == null) {
				return null;
			}
			SQFCommandExpression thenExp = (SQFCommandExpression) postArg.getExpr().withoutParenthesis();
			if (!thenExp.commandNameEquals("then")
					&& !thenExp.commandNameEquals("exitWith")) {
				return null;
			}
			{ //get condition
				SQFCommandArgument thenExpPrefix = thenExp.getPrefixArgument();
				if (thenExpPrefix == null) {
					return null;
				}
				condition = thenExpPrefix.getExpr().withoutParenthesis();
			}
			{ //get then block and else block
				SQFCommandArgument thenExpPostfix = thenExp.getPostfixArgument();
				if (thenExpPostfix == null) {
					return null;
				}
				SQFExpression afterThenExp = thenExpPostfix.getExpr();
				if (afterThenExp instanceof SQFArray) {
					//if condition then [{/*true cond*/}, {/*false cond*/}]

					SQFArray array = (SQFArray) afterThenExp;
					List<SQFExpression> arrExps = array.getExpressions();
					if (arrExps.size() < 1) {
						return null;
					}
					SQFExpression first = arrExps.get(0).withoutParenthesis();
					thenBlock = new SQFBlockOrExpression.Impl(first);
					if (arrExps.size() > 1) {
						SQFExpression second = arrExps.get(1).withoutParenthesis();
						elseBlock = new SQFBlockOrExpression.Impl(second);
					}
				} else {//if condition then {};
					Reference<SQFBlockOrExpression> thenBlockRef = new Reference<>(null);
					Reference<SQFBlockOrExpression> elseBlockRef = new Reference<>(null);

					Function<Void, Void> getElseBlock = aVoid -> {
						if (!(afterThenExp.withoutParenthesis() instanceof SQFCommandExpression)) {
							return null;
						}
						SQFCommandExpression afterThenCmdExpr = (SQFCommandExpression) afterThenExp.withoutParenthesis();
						if (!afterThenCmdExpr.commandNameEquals("else")) {
							return null;
						}
						thenBlockRef.setValue(afterThenCmdExpr.getPrefixArgument());
						elseBlockRef.setValue(afterThenCmdExpr.getPostfixArgument());
						return null;
					};
					getElseBlock.apply(null);
					if (thenBlockRef.getValue() == null) {
						thenBlock = new SQFBlockOrExpression.Impl(afterThenExp);
					} else {
						thenBlock = thenBlockRef.getValue();
					}
					elseBlock = elseBlockRef.getValue();
				}
			}
		}
		if (thenBlock == null) {
			return null;
		}
		return new SQFIfHelperStatement(condition, thenBlock, elseBlock);
	}

	/**
	 * This will return a {@link SQFSpawnHelperStatement} only if the statement takes the form: "args <b>spawn</b> {}".
	 * It can also take the form var=args spawn {} (notice its in an assignment this time)
	 * <p>
	 * "args" is optional, but the argument after the spawn command must exist.
	 *
	 * @return the {@link SQFSpawnHelperStatement} instance if the statement contains a "args <b>spawn</b> {}" statement,
	 * or null if the statement isn't a valid spawn statement
	 */
	@Nullable
	default SQFSpawnHelperStatement getSpawnStatement() {
		SQFExpression expr;
		if (this instanceof SQFExpressionStatement) {
			expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
		} else if (this instanceof SQFAssignmentStatement) {
			expr = ((SQFAssignmentStatement) this).getExpr();
			if (expr == null) {
				return null;
			}
			expr = expr.withoutParenthesis();
		} else {
			return null;
		}
		if (!(expr instanceof SQFCommandExpression)) {
			return null;
		}
		SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
		if (!cmdExpr.commandNameEquals("spawn")) {
			return null;
		}
		SQFCommandArgument spawnPostArg = cmdExpr.getPostfixArgument();
		if (spawnPostArg == null) {
			return null;
		}
		SQFCommandArgument spawnPreArg = cmdExpr.getPrefixArgument();
		return new SQFSpawnHelperStatement(spawnPreArg, spawnPostArg);
	}

	/**
	 * @return the {@link SQFSwitchHelperStatement} instance if the statement contains a switch statement,
	 * or null if the statement isn't a valid switch statement
	 */
	@Nullable
	default SQFSwitchHelperStatement getSwitchStatement() {
		SQFExpression expr;
		if (this instanceof SQFExpressionStatement) {
			expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
		} else if (this instanceof SQFAssignmentStatement) {
			expr = ((SQFAssignmentStatement) this).getExpr();
			if (expr == null) {
				return null;
			}
			expr = expr.withoutParenthesis();
		} else {
			return null;
		}
		if (!(expr instanceof SQFCommandExpression)) {
			return null;
		}
		SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
		if (!cmdExpr.commandNameEquals("switch")) {
			return null;
		}
		SQFCommandArgument postArg = cmdExpr.getPostfixArgument();
		if (postArg == null) {
			return null;
		}
		SQFScope switchBlockScope = null;
		List<SQFCaseStatement> caseStatements = new ArrayList<>();
		Reference<SQFBlockOrExpression> blockOrExprRef = new Reference<>(null);
		{ //get case statements as well as default statement
			SQFCodeBlock block = postArg.getBlock();
			if (block == null) {
				return null;
			}
			switchBlockScope = block.getScope();
			if (switchBlockScope != null) {
				PsiUtil.traverseInLayers(switchBlockScope.getNode(), astNode -> {
					PsiElement nodeAsElement = astNode.getPsi();
					if (nodeAsElement instanceof SQFCaseStatement) {
						caseStatements.add((SQFCaseStatement) nodeAsElement);
					} else if (nodeAsElement instanceof SQFExpressionStatement) {
						SQFExpressionStatement exprStatement = (SQFExpressionStatement) nodeAsElement;
						SQFExpression exprInExprStatement = exprStatement.getExpr();
						if (exprInExprStatement instanceof SQFCommandExpression) {
							SQFCommandExpression cmdExprInSwitch = (SQFCommandExpression) exprInExprStatement;
							if (cmdExprInSwitch.commandNameEquals("default")) {
								blockOrExprRef.setValue(cmdExprInSwitch.getPostfixArgument());
							}
						}
					}
					return true; //don't traverse past children
				});
			}
		}

		return new SQFSwitchHelperStatement(caseStatements, blockOrExprRef.getValue());
	}

	/**
	 * @return the {@link SQFForLoopHelperStatement} instance if the statement contains a for loop statement,
	 * or null if the statement isn't a valid for loop statement
	 */
	@Nullable
	default SQFForLoopHelperStatement getForLoopStatement() {
		//todo
		return null;
	}

	/**
	 * @return the {@link SQFWhileLoopHelperStatement} instance if the statement contains a while loop statement,
	 * or null if the statement isn't a valid for while statement
	 */
	@Nullable
	default SQFWhileLoopHelperStatement getWhileLoopStatement() {
		SQFExpression expr;
		if (this instanceof SQFExpressionStatement) {
			expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
		} else {
			return null;
		}
		if (!(expr instanceof SQFCommandExpression)) {
			return null;
		}
		SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
		if (!cmdExpr.commandNameEquals("while")) {
			return null;
		}
		SQFCommandArgument whilePostArg = cmdExpr.getPostfixArgument();
		if (whilePostArg == null) {
			return null;
		}
		SQFExpression whilePostArgExpr = whilePostArg.getExpr();
		whilePostArgExpr = whilePostArgExpr.withoutParenthesis();
		if (!(whilePostArgExpr instanceof SQFCommandExpression)) {
			return null;
		}
		SQFCommandExpression cmdDoExpr = (SQFCommandExpression) whilePostArgExpr;
		SQFCommandArgument whileCondition = cmdDoExpr.getPrefixArgument();
		SQFCommandArgument whileBody = cmdDoExpr.getPostfixArgument();
		if (whileCondition == null || whileBody == null) {
			return null;
		}
		return new SQFWhileLoopHelperStatement(whileCondition, whileBody);
	}

	/**
	 * @return the {@link SQFControlStructure} instance if the statement contains a control structure,
	 * or null if the statement isn't a valid control structure
	 */
	@Nullable
	default SQFControlStructure getControlStructure() {
		SQFControlStructure cs = getIfStatement();
		if (cs != null) {
			return cs;
		}
		cs = getForLoopStatement();
		if (cs != null) {
			return cs;
		}
		cs = getWhileLoopStatement();
		if (cs != null) {
			return cs;
		}
		cs = getSwitchStatement();
		return cs;
	}

	/**
	 * Used for debugging. Will return the statement the given element is contained in.
	 * If the element is a PsiComment, &lt;PsiComment&gt; will be returned. Otherwise, the element's ancestor statement
	 * text will be returned with all newlines replaced with spaces.
	 * <p>
	 * If the element has no parent or no {@link SQFStatement} parent, &lt;No Statement Parent&gt; will be returned
	 *
	 * @return the text, or &lt;PsiComment&gt; if element is a PsiComment
	 */
	@NotNull
	static String getStatementTextForElement(@NotNull PsiElement element) {
		if (element instanceof PsiComment) {
			return "<PsiComment>";
		}
		if (element.getContainingFile() == null || !(element.getContainingFile() instanceof SQFFile)) {
			throw new IllegalArgumentException("element isn't in an SQFFile");
		}
		while (!(element instanceof SQFStatement)) {
			element = element.getParent();
			if (element == null) {
				return "<No Statement Parent>";
			}
		}
		return element.getText().replaceAll("\n", " ");
	}
}
