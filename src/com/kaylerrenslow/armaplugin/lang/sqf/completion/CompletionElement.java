package com.kaylerrenslow.armaplugin.lang.sqf.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 03/28/2016
 */
public interface CompletionElement extends InsertHandler<LookupElement> {
	@NotNull
	LookupElement getLookupElement(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result);
}

