package com.kaylerrenslow.armaplugin.lang.sqf.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaplugin.lang.ArmaPluginUserData;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 09/09/2017
 */
public class SQFFunctionNameCompletionProvider extends CompletionProvider<CompletionParameters> {
	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		HeaderFile headerFile = ArmaPluginUserData.getInstance().getRootConfigHeaderFile(parameters.getOriginalFile());
		if (headerFile != null) {
			System.out.println(headerFile.getAsString());
		}
	}
}
