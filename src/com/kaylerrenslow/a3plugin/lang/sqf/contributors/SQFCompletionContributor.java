package com.kaylerrenslow.a3plugin.lang.sqf.contributors;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFLanguage;
import com.kaylerrenslow.a3plugin.lang.sqf.providers.SQFCompletionProvider;
import com.kaylerrenslow.a3plugin.lang.sqf.providers.SQFDocTagsCompletionProvider;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;

/**
 * Provides auto completion for SQF related editing
 *
 * @author Kayler
 * @since 01/02/2016
 */
public class SQFCompletionContributor extends CompletionContributor {
	public SQFCompletionContributor() {
		SQFCompletionProvider prov = new SQFCompletionProvider();
		extend(CompletionType.BASIC,
				PlatformPatterns.psiElement(SQFTypes.GLOBAL_VAR).withLanguage(SQFLanguage.INSTANCE),
				prov
		);
		extend(CompletionType.BASIC,
				PlatformPatterns.psiElement(SQFTypes.LOCAL_VAR).withLanguage(SQFLanguage.INSTANCE),
				prov
		);
		extend(CompletionType.BASIC,
				PlatformPatterns.psiComment().withLanguage(SQFLanguage.INSTANCE),
				new SQFDocTagsCompletionProvider()
		);
	}

}
