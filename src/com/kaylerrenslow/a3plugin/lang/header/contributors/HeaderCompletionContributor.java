package com.kaylerrenslow.a3plugin.lang.header.contributors;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;

/**
 * @author Kayler
 *         CompletionContributor implementation for Header language
 *         Created on 01/01/2016.
 */
public class HeaderCompletionContributor extends CompletionContributor {
	public HeaderCompletionContributor() {
		HeaderStringtableCompletionProvider prov = new HeaderStringtableCompletionProvider();

		extend(CompletionType.BASIC,
				PlatformPatterns.psiElement(HeaderTypes.IDENTIFIER).withLanguage(HeaderLanguage.INSTANCE),
				prov);
	}
}
