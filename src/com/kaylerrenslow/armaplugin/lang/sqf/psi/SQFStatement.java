package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaDialogCreator.util.Reference;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.util.DoubleArgFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public abstract class SQFStatement extends ASTWrapperPsiElement implements SQFSyntaxNode {

	public SQFStatement(@NotNull ASTNode node) {
		super(node);
	}

	/**
	 * Gets all private vars declared private in this statement. Note that private variables may be declared private in the
	 * statement, but not in the same scope as the statement!
	 * <p>
	 * This method will return an empty list if the statement can create private variables but none were made private.
	 *
	 * @param scopeHelper helper to store {@link SQFPrivateVar} instances
	 * @param privatizer  the privatizer this {@link SQFStatement} is contained in,
	 *                    or null if this {@link SQFStatement} isn't in one. This is to help determine
	 *                    implicit private variables in control structures and spawn statements.
	 */
	public void searchForDeclaredPrivateVars(@NotNull SQFScope.VariableScopeHelper scopeHelper,
											 @Nullable SQFImplicitPrivatizer privatizer) {
		SQFScope maxScope;
		List<SQFScope> mergeScopes = Collections.emptyList();
		if (privatizer == null) {
			maxScope = SQFScope.getContainingScope(this);
		} else {
			maxScope = privatizer.getImplicitPrivateScope();
			List<SQFScope> ctrlStructMergeScopes = privatizer.getMergeScopes();
			if (!ctrlStructMergeScopes.isEmpty()) {
				mergeScopes = new ArrayList<>();
				mergeScopes.addAll(privatizer.getMergeScopes());
			}
		}
		Set<SQFPrivateVar> privateVarsSet = scopeHelper.getPrivateVarsForScope(maxScope);


		/*
		* Traverse SQFCodeBlock expressions, but not their children
		* because each call to checkCodeBlockExpr will check the code block's children
		*/
		final DoubleArgFunction<SQFExpression, SQFImplicitPrivatizer, Void> findAllCodeExpr = (expr, implPriv) -> {
			PsiUtil.traverseInLayers(expr.getNode(), astNode -> {
				PsiElement nodeAsElement = astNode.getPsi();
				if (nodeAsElement instanceof SQFCodeBlockExpression) {
					SQFCodeBlockExpression codeBlockExpression = ((SQFCodeBlockExpression) nodeAsElement);
					collectPrivateVarsFromCodeBlockExp(scopeHelper, codeBlockExpression, implPriv);
					return true; //don't add children
				}
				return false;
			});
			return null;
		};

		if (this instanceof SQFAssignmentStatement) {
			SQFAssignmentStatement assignment = (SQFAssignmentStatement) this;
			if (assignment.isPrivate()) {
				privateVarsSet.add(
						new SQFExplicitPrivateVar(assignment.getVar().getVarNameObj(), assignment.getVar(), maxScope, mergeScopes)
				);
			}
			if (assignment.getExpr() != null) {
				SQFExpression assignmentExpr = assignment.getExpr();
				findAllCodeExpr.apply(assignmentExpr, privatizer);
			}
		}
		if (this instanceof SQFExpressionStatement) {
			SQFExpression expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
			if (expr instanceof SQFCommandExpression) {
				SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
				switch (cmdExpr.getSQFCommand().getCommandName().toLowerCase()) {
					case "private": {
						SQFCommandArgument postfixArgument = cmdExpr.getPostfixArgument();
						if (postfixArgument == null) {
							break;
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
												privateVarsSet.add(
														new SQFExplicitPrivateVar(
																new SQFVariableName(str.getNonQuoteText()),
																str, maxScope, mergeScopes
														)
												);
											}
										}
									}
								}
							} else if (literal.getStr() != null) {
								//private "_var"
								if (literal.getStr().containsLocalVariable()) {
									privateVarsSet.add(
											new SQFExplicitPrivateVar(
													new SQFVariableName(literal.getStr().getNonQuoteText()),
													literal.getStr(),
													maxScope, mergeScopes
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

					//control structures
					case "if": {
						SQFIfHelperStatement ifStatement = getIfStatement();
						if (ifStatement == null) {
							break;
						}
						SQFBlockOrExpression then = ifStatement.getThen();
						if (then.getBlock() != null) {
							collectPrivateVarsFromCodeBlock(scopeHelper, then.getBlock(), ifStatement);
						}
						SQFBlockOrExpression else1 = ifStatement.getElse();
						if (else1 != null) {
							if (else1.getBlock() != null) {
								collectPrivateVarsFromCodeBlock(scopeHelper, else1.getBlock(), ifStatement);
							}
						}
						break;
					}
					case "while": {//todo
						break;
					}
					case "for": {//todo
						break;
					}
					case "switch": {//todo
						break;
					}
					default: {
						collectImplicitPrivateVars(scopeHelper, privatizer);
						break;
					}
				}
			} else {
				collectImplicitPrivateVars(scopeHelper, privatizer);
				findAllCodeExpr.apply(expr, privatizer);
			}
		}
	}

	private void collectPrivateVarsFromCodeBlockExp(SQFScope.VariableScopeHelper helper, SQFCodeBlockExpression codeBlockExpr, SQFImplicitPrivatizer implPriv) {
		SQFCodeBlock codeBlock = codeBlockExpr.getBlock();
		collectPrivateVarsFromCodeBlock(helper, codeBlock, implPriv);
	}

	private void collectPrivateVarsFromCodeBlock(@NotNull SQFScope.VariableScopeHelper scopeHelper, SQFCodeBlock codeBlock, SQFImplicitPrivatizer implPriv) {
		SQFLocalScope codeBlockScope = codeBlock.getScope();
		if (codeBlockScope != null) {
			//don't check containing scope because we already checked the assignment's scope, which is containing scope
			codeBlockScope.searchForPrivateVarInstances(false, scopeHelper, implPriv);
		}
	}

	private void collectImplicitPrivateVars(@NotNull SQFScope.VariableScopeHelper scopeHelper, @Nullable SQFImplicitPrivatizer privatizer) {
		if (privatizer == null) {
			return;
		}

		PsiUtil.traverseDepthFirstSearch(this.getNode(), astNode -> {
			PsiElement nodeAsElement = astNode.getPsi();
			if (nodeAsElement instanceof SQFVariable) {
				SQFVariable var = (SQFVariable) nodeAsElement;

				scopeHelper.getImplicitPrivateVarsSet().add(
						new SQFImplicitPrivateVar(var, privatizer)
				);
			}
			return false;
		});
	}

	/**
	 * @return the {@link SQFIfHelperStatement} instance if the statement contains an if statement,
	 * or null if the statement isn't a valid if statement
	 */
	@Nullable
	public SQFIfHelperStatement getIfStatement() {
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
			SQFExpression postArgExpr = postArg.getExpr().withoutParenthesis();
			if (!(postArgExpr instanceof SQFCommandExpression)) {
				return null;
			}

			SQFCommandExpression condExpr = (SQFCommandExpression) postArgExpr;
			condition = condExpr;

			postArg = condExpr.getPostfixArgument();
			if (postArg == null) {
				return null;
			}
			postArgExpr = postArg.getExpr().withoutParenthesis();
			if (!(postArgExpr instanceof SQFCommandExpression)) {
				return null;
			}

			SQFCommandExpression thenExp = (SQFCommandExpression) postArgExpr;
			if (!thenExp.commandNameEquals("then")
					&& !thenExp.commandNameEquals("exitWith")) {
				return null;
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

					//using a lambda so we can use return instead of lots of nested if statements!!
					final Function<Void, Void> getElseBlock = aVoid -> {
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
	public SQFSpawnHelperStatement getSpawnStatement() {
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
	public SQFSwitchHelperStatement getSwitchStatement() {
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
	public SQFForLoopHelperStatement getForLoopStatement() {
		//todo
		return null;
	}

	/**
	 * @return the {@link SQFWhileLoopHelperStatement} instance if the statement contains a while loop statement,
	 * or null if the statement isn't a valid for while statement
	 */
	@Nullable
	public SQFWhileLoopHelperStatement getWhileLoopStatement() {
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
	public SQFControlStructure getControlStructure() {
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
	 * @return the nearest ancestor {@link SQFStatement} that contains the given element, or null if not in a {@link SQFStatement}
	 * @throws IllegalArgumentException when element is a PsiComment or when it is not in an SQFFile
	 */
	@Nullable
	public static SQFStatement getStatementForElement(@NotNull PsiElement element) {
		if (element instanceof PsiComment) {
			throw new IllegalArgumentException("element is a comment");
		}
		if (element.getContainingFile() == null || !(element.getContainingFile() instanceof SQFFile)) {
			throw new IllegalArgumentException("element isn't in an SQFFile");
		}
		while (!(element instanceof SQFStatement)) {
			element = element.getParent();
			if (element == null) {
				return null;
			}
		}
		return (SQFStatement) element;
	}

	/**
	 * @return {@link #debug_getStatementTextForElement(PsiElement)} with this
	 */
	public String debug_getStatementTextForElement() {
		return debug_getStatementTextForElement(this);
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
	public static String debug_getStatementTextForElement(@NotNull PsiElement element) {
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
