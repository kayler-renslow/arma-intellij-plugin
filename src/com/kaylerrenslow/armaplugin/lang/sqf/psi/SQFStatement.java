package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
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
	 * @return the {@link SQFExpression} contained in this statement, or null if doesn't have one
	 */
	@Nullable
	default SQFExpression getExpr() {
		return PsiTreeUtil.getChildOfType(this, SQFExpression.class);
	}


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
		if (this.getExpr() instanceof SQFCommandExpression) {
			SQFCommandExpression expr = (SQFCommandExpression) this.getExpr();
			List<SQFPrivateVar> vars = new ArrayList<>();
			switch (expr.getSQFCommand().getCommandName().toLowerCase()) {
				case "private": {
					SQFCommandArgument postfixArgument = expr.getPostfixArgument();
					if (postfixArgument != null && postfixArgument.getExpr() instanceof SQFLiteralExpression) {
						SQFLiteralExpression literal = (SQFLiteralExpression) postfixArgument.getExpr();
						if (literal.getArr() != null) {
							//private ["_var"]
							for (SQFExpression arrExp : literal.getArr().getExpressions()) {
								if (arrExp instanceof SQFLiteralExpression) {
									SQFLiteralExpression arrExpLiteral = (SQFLiteralExpression) arrExp;
									if (arrExpLiteral.getStr() != null) {
										SQFString str = arrExpLiteral.getStr();
										if (str.getNonQuoteText().startsWith("_")) {
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
							if (literal.getStr().getNonQuoteText().startsWith("_")) {
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
		//todo we need to check inside code blocks, spawn, and control structures (https://community.bistudio.com/wiki/Variables#Scope)
		return null;
	}
}
