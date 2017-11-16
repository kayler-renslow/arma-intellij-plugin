package com.kaylerrenslow.armaplugin.lang.sqf;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.kaylerrenslow.armaplugin.lang.sqf.completion.SQFFunctionNameCompletionProvider;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFCommand;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFPsiCommandAfter;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFTypes;

import static com.intellij.patterns.PlatformPatterns.*;

/**
 * @author Kayler
 * @since 09/09/2017
 */
public class SQFCompletionContributor extends CompletionContributor {
	public SQFCompletionContributor() {
		extend(
				CompletionType.BASIC,
				PlatformPatterns.psiElement(SQFTypes.GLOBAL_VAR)
						.withAncestor(4,
								psiElement(SQFPsiCommandAfter.class).afterSibling(
										psiElement(SQFCommand.class)
												.withText(
														or(object("call"), object("spawn"))
												)
								)
						).withLanguage(SQFLanguage.INSTANCE),
				new SQFFunctionNameCompletionProvider()
		);
	}
}
