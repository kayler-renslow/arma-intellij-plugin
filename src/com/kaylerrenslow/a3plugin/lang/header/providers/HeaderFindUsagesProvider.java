package com.kaylerrenslow.a3plugin.lang.header.providers;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLexerAdapter;
import com.kaylerrenslow.a3plugin.lang.header.HeaderStatic;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderStringtableKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Created by Kayler on 06/25/2016.
 */
public class HeaderFindUsagesProvider implements FindUsagesProvider {
	@Nullable
	@Override
	public WordsScanner getWordsScanner() {
		return new DefaultWordsScanner(new HeaderLexerAdapter(), HeaderStatic.IDENTIFERS, HeaderStatic.COMMENTS, HeaderStatic.LITERALS);
	}

	@Override
	public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
		return psiElement instanceof PsiNamedElement;
	}

	@Nullable
	@Override
	public String getHelpId(@NotNull PsiElement psiElement) {
		return null;
	}

	@NotNull
	@Override
	public String getType(@NotNull PsiElement element) {
		return element instanceof HeaderStringtableKey ? "Stringtable Key" : "";
	}

	@NotNull
	@Override
	public String getDescriptiveName(@NotNull PsiElement element) {
		return element instanceof HeaderStringtableKey ? "Stringtable Key" : "";
	}

	@NotNull
	@Override
	public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
		return element.getText();
	}
}
