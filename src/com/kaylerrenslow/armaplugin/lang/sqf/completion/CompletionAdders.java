package com.kaylerrenslow.armaplugin.lang.sqf.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.armaplugin.ArmaPluginIcons;
import com.kaylerrenslow.armaplugin.ArmaPluginUserData;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunction;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

/**
 * @author Kayler
 * @since 12/10/2017
 */
public class CompletionAdders {
	private static int VAR_PRIORITY = 7000;
	private static int FUNCTION_PRIORITY = 8000;
	private static int LITERAL_PRIORITY = 9000;

	public static void addVariables(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result,
									@NotNull PsiElement cursor, boolean forLocalVars) {
		HashSet<String> putVars = new HashSet<>();
		PsiUtil.traverseDepthFirstSearch(parameters.getOriginalFile().getNode(), astNode -> {
			PsiElement nodeAsElement = astNode.getPsi();
			if (nodeAsElement == cursor) {
				return false;
			}
			if (!(nodeAsElement instanceof SQFVariable)) {
				return false;
			}
			SQFVariable var = (SQFVariable) nodeAsElement;
			if (((var.isLocal() && forLocalVars) || (!var.isLocal() && !forLocalVars)) && !putVars.contains(var.getVarName().toLowerCase())) {
				putVars.add(var.getVarName().toLowerCase());
				result.addElement(PrioritizedLookupElement.withPriority(
						LookupElementBuilder.createWithSmartPointer(var.getVarName(), var)
								.withTailText(var.isMagicVar() ? " (Magic Var)" : (
												forLocalVars ? " (Local Variable)" : " (Global Variable)"
										)
								)
								.withIcon(var.isMagicVar() ? ArmaPluginIcons.ICON_SQF_MAGIC_VARIABLE : ArmaPluginIcons.ICON_SQF_VARIABLE), VAR_PRIORITY)
				);
			}
			return false;
		});
	}

	/**
	 * Adds all literals to completion set for all parent commands expression at the cursor.
	 *
	 * @see CommandDescriptor#getAllLiterals()
	 */
	public static void addLiterals(@NotNull PsiElement cursor, @NotNull CompletionResultSet result, boolean trimQuotes) {
		ASTNode ancestor = PsiUtil.getFirstAncestorOfType(cursor.getNode(), SQFTypes.EXPRESSION_STATEMENT, null);
		if (ancestor == null) {
			return;
		}

		PsiUtil.traverseDepthFirstSearch(ancestor, astNode -> {
			PsiElement nodeAsPsi = astNode.getPsi();
			if (nodeAsPsi == cursor) {
				return true;
			}
			if (!(nodeAsPsi instanceof SQFCommandExpression)) {
				return false;
			}
			SQFCommandExpression commandExpression = (SQFCommandExpression) nodeAsPsi;
			SQFExpressionOperator operator = commandExpression.getExprOperator();

			CommandDescriptor descriptor = SQFSyntaxHelper.getInstance().getDescriptor(operator.getText());
			if (descriptor == null) {
				return false;
			}
			for (String s : descriptor.getAllLiterals()) {
				result.addElement(
						PrioritizedLookupElement.withPriority(LookupElementBuilder.create(trimQuotes ? s.substring(1, s.length() - 1) : s)
								.bold()
								.withTailText(" (" + SQFStatic.getSQFBundle().getString("CompletionContributors.literal") + ")")
								, LITERAL_PRIORITY
						)
				);
			}

			return false;
		});
	}

	/**
	 * Adds all description.ext/config.cpp functions to the completion result
	 */
	public static void addFunctions(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
		List<HeaderConfigFunction> allConfigFunctions = ArmaPluginUserData.getInstance().getAllConfigFunctions(parameters.getOriginalFile());
		if (allConfigFunctions == null) {
			return;
		}
		for (HeaderConfigFunction function : allConfigFunctions) {
			result.addElement(
					PrioritizedLookupElement.withPriority(
							LookupElementBuilder.create(function)
									.withIcon(HeaderConfigFunction.getIcon())
									.withPresentableText(function.getCallableName()), FUNCTION_PRIORITY
					)
			);
		}
	}

	/**
	 * Adds all SQF commands to the completion result
	 */
	public static void addCommands(@NotNull Project project, @NotNull CompletionResultSet result) {
		for (String command : SQFStatic.LIST_COMMANDS) {
			SQFCommand cmd = PsiUtil.createElement(project, command, SQFFileType.INSTANCE, SQFCommand.class);
			if (cmd == null) {
				continue;
			}
			result.addElement(LookupElementBuilder.createWithSmartPointer(command, cmd)
					.withIcon(ArmaPluginIcons.ICON_SQF_COMMAND)
					.appendTailText(" (Command)", true)
			);
		}
	}

	/**
	 * Adds all SQF BIS functions to the result
	 */
	public static void addBISFunctions(@NotNull Project project, @NotNull CompletionResultSet result) {
		for (String functionName : SQFStatic.LIST_BIS_FUNCTIONS) {
			SQFVariable fnc = PsiUtil.createElement(project, functionName, SQFFileType.INSTANCE, SQFVariable.class);
			if (fnc == null) {
				continue;
			}
			result.addElement(LookupElementBuilder.createWithSmartPointer(functionName, fnc)
					.withIcon(ArmaPluginIcons.ICON_SQF_FUNCTION)
					.appendTailText(" Bohemia Interactive Function", true)
			);
		}
	}
}

