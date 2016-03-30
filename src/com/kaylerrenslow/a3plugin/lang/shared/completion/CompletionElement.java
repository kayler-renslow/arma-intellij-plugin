package com.kaylerrenslow.a3plugin.lang.shared.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * Created on 03/28/2016.
 */
public interface CompletionElement extends InsertHandler<LookupElement>{
	LookupElement getLookupElement(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result);
}
