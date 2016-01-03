package com.kaylerrenslow.plugin.lang.sqf.psi.helpers;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.kaylerrenslow.plugin.lang.sqf.SQFLanguage;
import com.kaylerrenslow.plugin.lang.sqf.psi.SQFTypes;

/**
 * Created by Kayler on 01/02/2016.
 */
public class SQFCompletionContributor extends CompletionContributor{
	public SQFCompletionContributor() {
		extend(CompletionType.BASIC,
				PlatformPatterns.psiElement(SQFTypes.IDENTIFIER).withLanguage(SQFLanguage.INSTANCE),
				new IdentifierCompletionProv()
		);
		extend(CompletionType.BASIC,
				PlatformPatterns.psiElement(SQFTypes.LOCAL_VAR).withLanguage(SQFLanguage.INSTANCE),
				new LocalVarCompletionProv()
		);
	}

}
