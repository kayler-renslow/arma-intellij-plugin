package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.NotNull;

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
			} else {
				findAllCodeExpr.apply(expr);
			}
		}

		//todo we need to check inside code blocks, spawn, and control structures (https://community.bistudio.com/wiki/Variables#Scope)
		return vars;
	}

	default boolean isIfStatement() {
		//todo
		return false;
	}

	default boolean isSpawnStatement() {
		//todo
		return false;
	}

	default boolean isSwitchStatement() {
		//todo
		return false;
	}

	default boolean isControlStructure() {
		//todo
		return false;
	}

	/**
	 * Used for debugging. Will return the statement the given element is contained in.
	 * If the element is a PsiComment, &lt;PsiComment&gt; will be returned. Otherwise, the element's ancestor statement
	 * text will be returned with all newlines replaced with spaces.
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
		}
		return element.getText().replaceAll("\n", " ");
	}
}
