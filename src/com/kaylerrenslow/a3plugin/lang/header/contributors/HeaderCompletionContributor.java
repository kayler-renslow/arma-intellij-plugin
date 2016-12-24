package com.kaylerrenslow.a3plugin.lang.header.contributors;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;

/**
 * CompletionContributor implementation for Header language
 *
 * @author Kayler
 * @since 01/01/2016
 */
public class HeaderCompletionContributor extends CompletionContributor {
	public HeaderCompletionContributor() {
		HeaderStringtableCompletionProvider prov = new HeaderStringtableCompletionProvider();

		extend(CompletionType.BASIC,
				PlatformPatterns.psiElement(HeaderTypes.IDENTIFIER).withLanguage(HeaderLanguage.INSTANCE),
				prov);
	}
}
