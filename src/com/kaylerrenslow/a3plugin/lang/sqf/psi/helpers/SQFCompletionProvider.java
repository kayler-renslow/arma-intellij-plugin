package com.kaylerrenslow.a3plugin.lang.sqf.psi.helpers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.psiUtil.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Kayler on 01/02/2016.
 */
public class SQFCompletionProvider extends com.intellij.codeInsight.completion.CompletionProvider<CompletionParameters>{

	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		PsiElement cursor = parameters.getOriginalPosition();
		PsiElement e = null;
//		ASTNode a = null;

		LinkedList<PsiElement> elements = PsiUtil.findElements(parameters.getOriginalFile(), SQFTypes.IDENTIFIER, cursor);
//		elements.addAll(PsiUtil.findElements(parameters.getOriginalFile(), SQFTypes.LOCAL_VAR, cursor));
		Iterator<PsiElement> iter = elements.iterator();

		while(iter.hasNext()){
			e = iter.next();
			result.addElement(LookupElementBuilder.create(e.getText()));
		}
		for(int i = 0; i < SQFStatic.LIST_COMMANDS.size(); i++){
			result.addElement(LookupElementBuilder.create(SQFStatic.LIST_COMMANDS.get(i)));
		}
	}
}
