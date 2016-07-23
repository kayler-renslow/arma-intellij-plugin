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
 @author Kayler
 Provides completion for documentation tags like @command and @bis
 Created on 06/07/2016. */
public class SQFDocTagsCompletionProvider extends CompletionProvider<CompletionParameters> {
	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		PsiElement cursor = parameters.getOriginalPosition(); //cursor is on a word
		if (cursor == null) {
			return;
		}
		String commentText = cursor.getText();
		int caretPos = parameters.getEditor().getCaretModel().getPrimaryCaret().getOffset() - cursor.getTextOffset();
		if (commentText.charAt(caretPos - 1) == '@') {
			result.addElement(new CompletionElementWithTextReplace("@command", "command", 7, Plugin.resources.getString("lang.shared.auto_completion.tags.trail_text.command")).getLookupElement(parameters, context, result));
			result.addElement(new CompletionElementWithTextReplace("@bis", "bis", 3, Plugin.resources.getString("lang.shared.auto_completion.tags.trail_text.bis")).getLookupElement(parameters, context, result));
			result.addElement(new CompletionElementWithTextReplace("@fnc", "fnc", 3, Plugin.resources.getString("lang.shared.auto_completion.tags.trail_text.fnc")).getLookupElement(parameters, context, result));
		}
	}
}
