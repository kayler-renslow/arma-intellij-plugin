package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFLexerAdapter;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFCommand;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFString;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * Finds usages provider implementation for SQF language
 * Created on 03/19/2016.
 */
public class SQFFindUsagesProvider implements FindUsagesProvider{
	@Nullable
	@Override
	public WordsScanner getWordsScanner() {
		return new DefaultWordsScanner(new SQFLexerAdapter(), SQFStatic.IDENTIFIERS, SQFStatic.COMMENTS, SQFStatic.NUMBER_LITERALS);
	}

	@Override
	public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
		return psiElement instanceof PsiNamedElement || psiElement instanceof SQFString;
	}

	@Nullable
	@Override
	public String getHelpId(@NotNull PsiElement psiElement) {
		if(psiElement instanceof SQFVariable){
			SQFVariable var = (SQFVariable)psiElement;
			if(var.followsSQFFunctionNameRules()){
				return "Function";
			}
			return "Value read";
		}
		if(psiElement instanceof SQFString){
			return "SQF String";
		}
		return "SQF";
	}

	@NotNull
	@Override
	public String getType(@NotNull PsiElement element) {
		if(element instanceof SQFVariable){
			SQFVariable var = (SQFVariable)element;
			if(var.followsSQFFunctionNameRules()){
				return "Function";
			}
			return "Variable";
		}
		if(element instanceof SQFCommand){
			return "Command";
		}
		if(element instanceof SQFString){
			return "String";
		}
		return "unknown type";
	}

	@NotNull
	@Override
	public String getDescriptiveName(@NotNull PsiElement element) {
		if(element instanceof SQFVariable){
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
