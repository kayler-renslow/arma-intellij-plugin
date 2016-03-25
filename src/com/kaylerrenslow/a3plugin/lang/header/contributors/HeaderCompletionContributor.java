package com.kaylerrenslow.a3plugin.lang.header.contributors;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Kayler on 01/01/2016.
 */
public class HeaderCompletionContributor extends CompletionContributor {
	public HeaderCompletionContributor() {
//		extend(CompletionType.BASIC,
//				PlatformPatterns.psiElement(HeaderTypes.IDENTIFIER).withLanguage(HeaderLanguage.INSTANCE),
//				new CompletionProv()
//				);
	}


}
//class CompletionProv extends CompletionProvider<CompletionParameters>{
//
//	@Override
//	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
//		result.addElement(LookupElementBuilder.create("duuuuuuude"));
//	}
//
//}
