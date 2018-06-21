package com.kaylerrenslow.armaplugin.lang.sqf;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.armaplugin.lang.sqf.completion.*;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFCommand;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFPsiCommandAfter;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.*;

/**
 * @author Kayler
 * @since 09/09/2017
 */
public class SQFCompletionContributor extends CompletionContributor {
	public SQFCompletionContributor() {
		extend(CompletionType.BASIC,
				PlatformPatterns.psiElement(SQFTypes.GLOBAL_VAR).withLanguage(SQFLanguage.INSTANCE),
				new SQFVariableCompletionProvider(false)
		);
		extend(CompletionType.BASIC,
				PlatformPatterns.psiElement(SQFTypes.LOCAL_VAR).withLanguage(SQFLanguage.INSTANCE),
				new SQFVariableCompletionProvider(true)
		);
		extend(CompletionType.BASIC,
				PlatformPatterns.psiElement(SQFTypes.STRING_LITERAL).withLanguage(SQFLanguage.INSTANCE),
				new CompletionProvider<CompletionParameters>() {
					@Override
					protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext processingContext, @NotNull CompletionResultSet result) {
						PsiElement cursor = parameters.getOriginalPosition(); //cursor is on a word

						boolean originalPositionNull = cursor == null;
						if (originalPositionNull) {
							cursor = parameters.getPosition(); //cursor is after a word
						}
						CompletionAdders.addLiterals(cursor, result, true);
					}
				}
		);

		extend(CompletionType.BASIC,
				PlatformPatterns.psiComment().withLanguage(SQFLanguage.INSTANCE),
				new SQFDocTagsCompletionProvider()
		);

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

		extend(
				CompletionType.BASIC,
				PlatformPatterns.psiElement(SQFTypes.GLOBAL_VAR)
						.withAncestor(4,
								psiElement(SQFPsiCommandAfter.class).afterSibling(
										psiElement(SQFCommand.class)
												.withText(
														object("localize")
												)
								)
						).withLanguage(SQFLanguage.INSTANCE),
				new SQFLocalizeCompletionProvider()
		);
	}
}
