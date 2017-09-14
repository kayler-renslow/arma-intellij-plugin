package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public interface SQFStatement extends PsiElement {

	/**
	 * Gets all private vars declared private in this statement.
	 * This method will return null if the statement is incapable of making variables private. This method will return an empty list
	 * if the statement can create private variables but none were made private.
	 *
	 * @return list of private vars, or null if can't create private vars
	 */
	@Nullable
	default List<SQFPrivateVar> getDeclaredPrivateVars() {
		SQFScope containingScope = SQFScope.getContainingScope(this);
		if (this instanceof SQFAssignmentStatement) {
			SQFAssignmentStatement assignment = (SQFAssignmentStatement) this;
			if (assignment.isPrivate()) {
				return Collections.singletonList(new SQFPrivateVar(assignment.getVar().getVarNameObj(), assignment.getVar(), containingScope));
			}
		}
		if (this instanceof SQFExpressionStatement) {
			SQFExpression expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
			if (expr instanceof SQFCommandExpression) {
				SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
				List<SQFPrivateVar> vars = new ArrayList<>();
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
