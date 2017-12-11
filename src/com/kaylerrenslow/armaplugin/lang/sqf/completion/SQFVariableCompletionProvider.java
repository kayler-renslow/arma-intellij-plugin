package com.kaylerrenslow.armaplugin.lang.sqf.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.armaplugin.ArmaPluginIcons;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;

/**
 * Used for completing variables and commands
 *
 * @author Kayler
 * @since 12/10/2017
 */
public class SQFVariableCompletionProvider extends CompletionProvider<CompletionParameters> {
	private final boolean forLocalVars;

	/**
	 * @param forLocalVars true if this completion provider is meant for local variables.
	 *                     False if for commands and global variables.
	 */
	public SQFVariableCompletionProvider(boolean forLocalVars) {
		this.forLocalVars = forLocalVars;
	}

	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		PsiElement cursor = parameters.getOriginalPosition(); //cursor is on a word
		Project project = parameters.getOriginalFile().getProject();

		boolean originalPositionNull = cursor == null;
		if (originalPositionNull) {
			cursor = parameters.getPosition(); //cursor is after a word
		}
		if (forLocalVars) {
			if (cursor.getText().startsWith("_fnc")) {
				CompletionAdders.addFunctions(parameters, result);
			}
			addVariables(parameters, result, cursor);
		} else {
			if (cursor.getText().startsWith("BIS_")) {
				CompletionAdders.addBISFunctions(project, result);
			} else {
				addVariables(parameters, result, cursor);
				CompletionAdders.addCommands(project, result);
			}
		}
	}

	private void addVariables(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, @NotNull PsiElement cursor) {
		PsiUtil.traverseDepthFirstSearch(parameters.getOriginalFile().getNode(), astNode -> {
			PsiElement nodeAsElement = astNode.getPsi();
			if (nodeAsElement == cursor) {
				return false;
			}
			if (!(nodeAsElement instanceof SQFVariable)) {
				return false;
			}
			SQFVariable var = (SQFVariable) nodeAsElement;
			if ((var.isLocal() && forLocalVars) || (!var.isLocal() && !forLocalVars)) {
				result.addElement(LookupElementBuilder.createWithSmartPointer(var.getVarName(), var)
						.withTailText(var.isMagicVar() ? " (Magic Var)" : (
										forLocalVars ? " (Local Variable)" : " (Global Variable)"
								)
						)
						.withIcon(var.isMagicVar() ? ArmaPluginIcons.ICON_SQF_MAGIC_VARIABLE : ArmaPluginIcons.ICON_SQF_VARIABLE)
				);
			}
			return false;
		});
	}
}
