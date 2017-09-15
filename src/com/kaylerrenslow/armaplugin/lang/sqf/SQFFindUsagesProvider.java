package com.kaylerrenslow.armaplugin.lang.sqf;


import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ResourceBundle;

/**
 * Finds usages provider implementation for SQF language
 *
 * @author Kayler
 * @since 09/14/2017
 */
public class SQFFindUsagesProvider implements FindUsagesProvider {
	@Nullable
	@Override
	public WordsScanner getWordsScanner() {
		return new DefaultWordsScanner(
				new SQFLexerAdapter(),
				SQFParserDefinition.IDENTIFIERS,
				SQFParserDefinition.COMMENTS,
				SQFParserDefinition.NUMBER_LITERALS
		);
	}

	@Override
	public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
		return psiElement instanceof PsiNamedElement || psiElement instanceof SQFString;
	}

	@Nullable
	@Override
	public String getHelpId(@NotNull PsiElement psiElement) {
		ResourceBundle bundle = SQFStatic.getSQFBundle();
		if (psiElement instanceof SQFVariable) {
			SQFVariable var = (SQFVariable) psiElement;
			if (var.followsSQFFunctionNameRules()) {
				return bundle.getString("FindUsagesProvider.HelpId.function");
			}
			return bundle.getString("FindUsagesProvider.HelpId.value_read");
		}
		if (psiElement instanceof SQFString) {
			return bundle.getString("FindUsagesProvider.HelpId.string");
		}
		return "SQF";
	}

	@NotNull
	@Override
	public String getType(@NotNull PsiElement element) {
		ResourceBundle bundle = SQFStatic.getSQFBundle();
		if (element instanceof SQFVariable) {
			SQFVariable var = (SQFVariable) element;
			if (var.followsSQFFunctionNameRules()) {
				return bundle.getString("FindUsagesProvider.Type.function");
			}
			return bundle.getString("FindUsagesProvider.Type.variable");
		}
		if (element instanceof SQFCommand) {
			return bundle.getString("FindUsagesProvider.Type.command");
		}
		if (element instanceof SQFString) {
			return bundle.getString("FindUsagesProvider.Type.string");
		}
		return bundle.getString("FindUsagesProvider.Type.unknown");
	}

	@NotNull
	@Override
	public String getDescriptiveName(@NotNull PsiElement element) {
		if (element instanceof SQFVariable) {
			return element.getNode().getText();
		}
		return getClass().getName() + " getDescriptiveName";
	}

	@NotNull
	@Override
	public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
		return element.getText(); //text to display in find usages window beneath list
	}
}