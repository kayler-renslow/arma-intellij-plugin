package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public interface SQFStatement extends PsiElement {

	/**
	 * Gets all private vars declared private in this statement. Note that private variables may be declared private in the
	 * statement, but not in the same scope as the statement!
	 * <p>
	 * This method will return null if the statement is incapable of making variables private. This method will return an empty list
	 * if the statement can create private variables but none were made private.
	 *
	 * @return list of private vars, or null if can't create private vars
	 */
	@Nullable
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
					boolean alreadyPrivate = false;
					for (SQFPrivateVar statementPrivateVar : vars) {
						if (blockPrivateVar.getVariableNameObj().nameEquals(statementPrivateVar.getVariableNameObj())
								&& blockPrivateVar.getMaxScope() == statementPrivateVar.getMaxScope()) {
							//we need to check scopes as well because a variable may be declared private in a scope
							//but that may not be it's max scope
							alreadyPrivate = true;
							break;
						}
					}
					if (!alreadyPrivate) {
						vars.add(blockPrivateVar);
					}
				}
			}
			return null;
		};

		if (this instanceof SQFAssignmentStatement) {
			SQFAssignmentStatement assignment = (SQFAssignmentStatement) this;
			if (assignment.isPrivate()) {
				vars.add(new SQFPrivateVar(assignment.getVar().getVarNameObj(), assignment.getVar(), containingScope));
			}
			if (assignment.getExpr() != null) {
				SQFExpression assignmentExpr = assignment.getExpr();
				PsiUtil.traverseBreadthFirstSearch(assignmentExpr.getNode(), astNode -> {
					PsiElement nodeAsPsi = astNode.getPsi();
					if (nodeAsPsi instanceof SQFCodeBlockExpression) {
						SQFCodeBlockExpression codeBlockExpression = ((SQFCodeBlockExpression) nodeAsPsi);
						checkCodeBlockExp.apply(codeBlockExpression);
						return false; //nothing left to do
					}
					if (nodeAsPsi instanceof SQFScope) {
						//do not traverse further because it was already traversed in SQFCodeBlockExpression
						return false;
					}
					return false;
				});
			}
			return vars;
		}
		if (this instanceof SQFExpressionStatement) {
			SQFExpression expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
			if (expr instanceof SQFCodeBlockExpression) {
				checkCodeBlockExp.apply((SQFCodeBlockExpression) expr);
				return vars;
			}
			if (expr instanceof SQFCommandExpression) {
				SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
				switch (cmdExpr.getSQFCommand().getCommandName().toLowerCase()) {
					case "private": {
						SQFCommandArgument postfixArgument = cmdExpr.getPostfixArgument();
						if (postfixArgument == null || postfixArgument.getExpr() == null) {
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
				}
				return vars;
			}
		}

		//todo we need to check inside code blocks, spawn, and control structures (https://community.bistudio.com/wiki/Variables#Scope)
		return null;
	}
}
