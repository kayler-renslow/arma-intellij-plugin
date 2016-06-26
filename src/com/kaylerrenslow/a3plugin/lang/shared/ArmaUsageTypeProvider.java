package com.kaylerrenslow.a3plugin.lang.shared;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderFile;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import org.jetbrains.annotations.Nullable;

/**
 @author Kayler
 When find usages is called by user, each type of usage is classified as something like 'assignment' or 'function call'. This class handles the naming of found usages.
 Created on 03/23/2016. */
public class ArmaUsageTypeProvider implements UsageTypeProvider {

	private static final UsageType TYPE_VAR_IN_STRING = new UsageType(Plugin.resources.getString("lang.shared.usage_type.var_in_string"));
	private static final UsageType TYPE_UNKNOWN_HEADER_USAGE = new UsageType(Plugin.resources.getString("lang.shared.usage_type.unknown_header"));
	private static final UsageType TYPE_UNKNOWN_SQF_USAGE = new UsageType(Plugin.resources.getString("lang.shared.usage_type.unknown_sqf"));
	private static final UsageType TYPE_ASSIGNMENT = new UsageType(Plugin.resources.getString("lang.shared.usage_type.assignment"));
	private static final UsageType TYPE_FUNCTION_CALL = new UsageType(Plugin.resources.getString("lang.shared.usage_type.function_call"));
	private static final UsageType TYPE_FUNCTION_SPAWN = new UsageType(Plugin.resources.getString("lang.shared.usage_type.function_spawn"));
	private static final UsageType TYPE_FUNCTION_PARAMETER = new UsageType(Plugin.resources.getString("lang.shared.usage_type.function_param"));
	private static final UsageType TYPE_RETURN_STATEMENT = new UsageType(Plugin.resources.getString("lang.shared.usage_type.return_statement"));
	private static final UsageType TYPE_COMMAND_ARGUMENT = new UsageType(Plugin.resources.getString("lang.shared.usage_type.command_argument"));
	private static final UsageType TYPE_COMMAND_GENERAL = new UsageType(Plugin.resources.getString("lang.shared.usage_type.command"));

	@Nullable
	@Override
	public UsageType getUsageType(PsiElement element) {
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
	private UsageType getUsageTypeForVariable(SQFVariable variable) {
		ASTNode statementNode = PsiUtil.getFirstAncestorOfType(variable.getNode(), SQFTypes.STATEMENT, null);
		if (statementNode != null) {
			SQFStatement statement = (SQFStatement) statementNode.getPsi();
			if (statement.getAssignment() != null) {
				SQFAssignment assignment = statement.getAssignment();
				if (assignment.getAssigningVariable() == variable) {
					return TYPE_ASSIGNMENT;
				}
			}
		}

		if (variable.getParent() instanceof SQFLiteralExpression) {
			if (variable.getParent().getParent() instanceof SQFCommandExpression) {
				SQFCommandExpression commandExpression = (SQFCommandExpression) variable.getParent().getParent();
				PsiElement postfixArg = commandExpression.getPostfixArgument();
				if (postfixArg != null && postfixArg.getText().equals(variable.getVarName())) {
					if (commandExpression.getCommandName().equals("call")) {
						return TYPE_FUNCTION_CALL;
					}
					if (commandExpression.getCommandName().equals("spawn")) {
						return TYPE_FUNCTION_SPAWN;
					}
					return TYPE_COMMAND_ARGUMENT;
				} else {
					if (commandExpression.getCommandName().equals("call")) {
						return TYPE_FUNCTION_PARAMETER;
					}
					if (commandExpression.getCommandName().equals("spawn")) {
						return TYPE_FUNCTION_PARAMETER;
					}
					return TYPE_COMMAND_ARGUMENT;
				}
			} else if (variable.getParent().getParent() instanceof SQFArrayEntry) {
				SQFArrayVal array = (SQFArrayVal) variable.getParent().getParent().getParent();
				if (array.getParent() instanceof SQFLiteralExpression && array.getParent().getParent() instanceof SQFCommandExpression) {
					SQFCommandExpression commandExpression = (SQFCommandExpression) array.getParent().getParent();
					if (commandExpression.getCommandName().equals("call") || commandExpression.getCommandName().equals("spawn")) {
						return TYPE_FUNCTION_PARAMETER;
					}
				}
			}

		}

		if (PsiUtil.isDescendantOf(variable.getNode(), SQFTypes.RETURN_STATEMENT, null)) {
			return TYPE_RETURN_STATEMENT;
		}

		return null; //let intellij decide the name
	}

}
