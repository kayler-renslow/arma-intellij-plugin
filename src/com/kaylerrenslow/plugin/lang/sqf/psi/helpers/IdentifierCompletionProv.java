package com.kaylerrenslow.plugin.lang.sqf.psi.helpers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.plugin.lang.psiUtil.PsiUtil;
import com.kaylerrenslow.plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.plugin.lang.sqf.psi.SQFTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Kayler on 01/02/2016.
 */
public class IdentifierCompletionProv extends CompletionProvider<CompletionParameters>{

	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		LinkedList<PsiElement> elements = PsiUtil.findElements(parameters.getOriginalFile(), SQFTypes.VARIABLE, parameters.getOriginalPosition());
		Iterator<PsiElement> iter = elements.iterator();

		PsiElement e;
		while(iter.hasNext()){
			e = iter.next();
			result.addElement(LookupElementBuilder.create(e.getText()));
		}
		for(int i = 0; i < SQFStatic.LIST_COMMANDS.size(); i++){
			result.addElement(LookupElementBuilder.create(SQFStatic.LIST_COMMANDS.get(i)));
		}
	}
}
