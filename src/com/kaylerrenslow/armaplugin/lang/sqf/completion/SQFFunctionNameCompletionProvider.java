package com.kaylerrenslow.armaplugin.lang.sqf.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.armaplugin.lang.ArmaPluginUserData;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @since 09/09/2017
 */
public class SQFFunctionNameCompletionProvider extends CompletionProvider<CompletionParameters> {
	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		List<HeaderConfigFunction> allConfigFunctions = ArmaPluginUserData.getInstance().getAllConfigFunctions(parameters.getOriginalFile());
		if (allConfigFunctions == null) {
			return;
		}
		for (HeaderConfigFunction function : allConfigFunctions) {
			result.addElement(LookupElementBuilder.create(function).withIcon(HeaderConfigFunction.getIcon()).withPresentableText(function.getCallableName()));
		}
	}
}
