package com.kaylerrenslow.armaplugin.lang;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaDialogCreator.util.Reference;
import com.kaylerrenslow.armaplugin.ArmaPlugin;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ResourceBundle;

/**
 * When find usages is called by user, each type of usage is classified as something like 'assignment' or 'function call'.
 * This class handles the naming of found usages.
 *
 * @author Kayler
 * @since 09/15/2017
 */
public class ArmaUsageTypeProvider implements UsageTypeProvider {

	private final UsageType TYPE_VAR_IN_STRING;
	private final UsageType TYPE_UNKNOWN_HEADER_USAGE;
	private final UsageType TYPE_UNKNOWN_SQF_USAGE;
	private final UsageType TYPE_ASSIGNMENT;
	private final UsageType TYPE_FUNCTION_CALL;
	private final UsageType TYPE_FUNCTION_SPAWN;
	private final UsageType TYPE_FUNCTION_PARAMETER;
	private final UsageType TYPE_RETURN_STATEMENT;
	private final UsageType TYPE_COMMAND_ARGUMENT;
	private final UsageType TYPE_COMMAND_GENERAL;

	public ArmaUsageTypeProvider() {
		ResourceBundle bundle = ArmaPlugin.getPluginBundle();
		TYPE_VAR_IN_STRING = new UsageType(bundle.getString("UsageTypeProvider.UsageType.var_in_string"));
		TYPE_UNKNOWN_HEADER_USAGE = new UsageType(bundle.getString("UsageTypeProvider.UsageType.unknown_header"));
		TYPE_UNKNOWN_SQF_USAGE = new UsageType(bundle.getString("UsageTypeProvider.UsageType.unknown_sqf"));
		TYPE_ASSIGNMENT = new UsageType(bundle.getString("UsageTypeProvider.UsageType.assignment"));
		TYPE_FUNCTION_CALL = new UsageType(bundle.getString("UsageTypeProvider.UsageType.function_call"));
		TYPE_FUNCTION_SPAWN = new UsageType(bundle.getString("UsageTypeProvider.UsageType.function_spawn"));
		TYPE_FUNCTION_PARAMETER = new UsageType(bundle.getString("UsageTypeProvider.UsageType.function_param"));
		TYPE_RETURN_STATEMENT = new UsageType(bundle.getString("UsageTypeProvider.UsageType.return_statement"));
		TYPE_COMMAND_ARGUMENT = new UsageType(bundle.getString("UsageTypeProvider.UsageType.command_argument"));
		TYPE_COMMAND_GENERAL = new UsageType(bundle.getString("UsageTypeProvider.UsageType.command_general"));
	}

	@Nullable
	@Override
	public UsageType getUsageType(@NotNull PsiElement element) {
		if (element instanceof SQFCommand) {
			return TYPE_COMMAND_GENERAL;
		}
		if (element instanceof SQFVariable) {
			UsageType x = getUsageTypeForVariable((SQFVariable) element);
			if (x != null) {
				return x;
			}
		}
		if (PsiUtil.isOfElementType(element.getNode(), SQFTypes.STRING)) {
			return TYPE_VAR_IN_STRING;
		}

		if (element.getContainingFile() instanceof SQFFile) {
			return TYPE_UNKNOWN_SQF_USAGE;
		} else if (element.getContainingFile() instanceof HeaderFile) {
			return TYPE_UNKNOWN_HEADER_USAGE;
		}
		return null;
	}

	@Nullable
	private UsageType getUsageTypeForVariable(@NotNull SQFVariable variable) {
		ASTNode statementNode = PsiUtil.getFirstAncestorOfType(variable.getNode(), SQFTypes.STATEMENT, null);
		if (statementNode != null) {
			SQFStatement statement = (SQFStatement) statementNode.getPsi();
			if (statement instanceof SQFAssignmentStatement) {
				SQFAssignmentStatement assignmentStatement = (SQFAssignmentStatement) statement;
				if (assignmentStatement.getVar() == variable) {
					return TYPE_ASSIGNMENT;
				}
			} else {
				//check if in return statement
				SQFScope containingScope = SQFScope.getContainingScope(statement);
				//don't use file scope because you can create functions with return statements like so: func={returnVar};

				int childInd = 0;
				PsiElement[] scopeChildren = containingScope.getChildren();
				for (PsiElement child : scopeChildren) {
					if (child == statement) {
						boolean lastStatement = true;
						for (int i = childInd; i < scopeChildren.length; i++) {
							if (scopeChildren[i] instanceof SQFStatement) {
								lastStatement = false;
								break;
							}
						}
						if (lastStatement) {
							return TYPE_RETURN_STATEMENT;
						}
					}
					childInd++;
				}
			}
		}
		{ //check if the variable is a part of a spawn or call command expression
			Reference<SQFCommandExpression> cmdExpr = new Reference<>(null);
			PsiUtil.traverseUp(variable.getNode(), astNode -> {
				PsiElement nodeAsPsi = astNode.getPsi();
				if (nodeAsPsi instanceof SQFStatement) {
					return true;
				}
				if (nodeAsPsi instanceof SQFCommandExpression) {
					cmdExpr.setValue((SQFCommandExpression) nodeAsPsi);
					return true;
				}
				return false;
			});
			if (cmdExpr.getValue() != null) {

				//used for something like "variable call {}" or "variable spawn {}"
				Reference<Boolean> parameter = new Reference<>(false);

				//used for something like "call variable" or "spawn variable"
				Reference<Boolean> funcExec = new Reference<>(false);

				//check arguments
				{
					SQFCommandArgument prefixArg = cmdExpr.getValue().getPrefixArgument();
					if (prefixArg != null && prefixArg.getExpr() != null) {
						SQFExpression prefixExp = prefixArg.getExpr().withoutParenthesis();
						if (prefixExp instanceof SQFLiteralExpression) {
							SQFLiteralExpression prefixLiteral = (SQFLiteralExpression) prefixExp;
							PsiUtil.traverseBreadthFirstSearch(prefixLiteral.getNode(), astNode -> {
								PsiElement nodeAsPsi = astNode.getPsi();
								if (nodeAsPsi == variable) {
									parameter.setValue(true);
									return true;
								}
								return false;
							});
						}
					}
				}

				//check function being executed
				{
					SQFCommandArgument postfixArg = cmdExpr.getValue().getPostfixArgument();
					if (postfixArg != null && postfixArg.getExpr() != null) {
						SQFExpression postfixExp = postfixArg.getExpr().withoutParenthesis();
						if (postfixExp instanceof SQFLiteralExpression) {
							SQFLiteralExpression postfixLiteral = (SQFLiteralExpression) postfixExp;
							if (postfixLiteral.getVar() == variable) {
								funcExec.setValue(true);
							}
						}
					}
				}

				switch (cmdExpr.getValue().getSQFCommand().getCommandName().toLowerCase()) {
					case "call": {
						if (funcExec.getValue() != null && funcExec.getValue()) {
							return TYPE_FUNCTION_CALL;
						} else if (parameter.getValue() != null && parameter.getValue()) {
							return TYPE_FUNCTION_PARAMETER;
						}
						return TYPE_COMMAND_ARGUMENT;
					}
					case "spawn": {
						if (funcExec.getValue() != null && funcExec.getValue()) {
							return TYPE_FUNCTION_SPAWN;
						} else if (parameter.getValue() != null && parameter.getValue()) {
							return TYPE_FUNCTION_PARAMETER;
						}
						return TYPE_COMMAND_ARGUMENT;
					}
				}
			}
		}

		return null; //let intellij decide the name
	}

}
