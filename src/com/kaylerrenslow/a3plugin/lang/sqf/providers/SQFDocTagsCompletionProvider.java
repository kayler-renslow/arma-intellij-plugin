package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.shared.completion.CompletionElementWithTextReplace;
import org.jetbrains.annotations.NotNull;

/**
 * Provides completion for documentation tags like @command and @bis
 *
 * @author Kayler
 * @since 06/07/2016
 */
public class SQFDocTagsCompletionProvider extends CompletionProvider<CompletionParameters> {
	private static final String COMMAND = "@command";
	private static final String BIS = "@bis";
	private static final String FNC = "@fnc";

	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		try {
			doAddCompletions(parameters, context, result);
		} catch (Exception ignore) {

		}
	}

	private void doAddCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		PsiElement cursor = parameters.getOriginalPosition(); //cursor is on a word
		if (cursor == null) {
			return;
		}
		String commentText = cursor.getText();
		int caretPos = parameters.getEditor().getCaretModel().getPrimaryCaret().getOffset() - cursor.getTextOffset();

		if (commentText.charAt(caretPos - 1) == '@') {
			result.addElement(new CompletionElementWithTextReplace(COMMAND, "command", 7, Plugin.resources.getString("lang.shared.auto_completion.tags.trail_text.command")).getLookupElement(parameters, context, result));
			result.addElement(new CompletionElementWithTextReplace(BIS, "bis", 3, Plugin.resources.getString("lang.shared.auto_completion.tags.trail_text.bis")).getLookupElement(parameters, context, result));
			result.addElement(new CompletionElementWithTextReplace(FNC, "fnc", 3, Plugin.resources.getString("lang.shared.auto_completion.tags.trail_text.fnc")).getLookupElement(parameters, context, result));
		} else {
			String s = commentText.substring(commentText.lastIndexOf('@', caretPos), caretPos);
			if (s.matches("@((bis)|(fnc)|(command))[ ][^\\s]*\\s$")) {
				return;
			}
			try {
				if (s.contains(COMMAND)) {
					CompletionAdders.addCommands(cursor.getProject(), result);
					return;
				}
			} catch (IndexOutOfBoundsException ignore) {

			}
			try {
				if (s.contains(BIS)) {
					CompletionAdders.addBISFunctions(cursor.getProject(), result);
					return;
				}
			} catch (IndexOutOfBoundsException ignore) {

			}
			try {
				if (s.contains(FNC)) {
					CompletionAdders.addFunctions(parameters, result);
					return;
				}
			} catch (IndexOutOfBoundsException ignore) {

			}

		}
	}
}
