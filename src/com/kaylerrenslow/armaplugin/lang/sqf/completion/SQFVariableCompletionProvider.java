package com.kaylerrenslow.armaplugin.lang.sqf.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
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
			CompletionAdders.addVariables(parameters, context, result, cursor, true);
		} else {
			if (cursor.getText().startsWith("BIS_")) {
				CompletionAdders.addBISFunctions(project, result);
			} else {
				CompletionAdders.addLiterals(cursor, result, false);
				CompletionAdders.addVariables(parameters, context, result, cursor, false);
				CompletionAdders.addCommands(project, result);
			}
		}
	}


}
