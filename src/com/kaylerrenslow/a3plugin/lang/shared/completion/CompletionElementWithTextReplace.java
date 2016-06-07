package com.kaylerrenslow.a3plugin.lang.shared.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * This class is responsible for creating Auto completion lookup elements while providing a base implementation for text replacing once the lookup element is selected.
 * Example use case is where user types 'elif'. When user presses enter, handleInsert is invoked and the text 'elif' is replaced with replaceString (defined in the constructor's parameters)
 * Created on 03/28/2016.
 */
public class CompletionElementWithTextReplace implements CompletionElement{

	protected String replaceStr, lookupStr, typeText;
	protected int newCursorPos;

	public CompletionElementWithTextReplace(String lookupStr, String replaceString, int newCursorPos, String typeText) {
		this.lookupStr = lookupStr;
		this.replaceStr = replaceString;
		this.typeText = typeText;
		this.newCursorPos = newCursorPos;
	}

	public CompletionElementWithTextReplace(){}

	@Override
	public LookupElement getLookupElement(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		return LookupElementBuilder.create(this.lookupStr).withInsertHandler(this).withTypeText(this.typeText);
	}

	@Override
	public void handleInsert(InsertionContext context, LookupElement item) {
		Runnable runnable = new Runnable(){
			@Override
			public void run() {
				context.getDocument().replaceString(context.getStartOffset(), context.getTailOffset(), replaceStr);
				context.getEditor().getCaretModel().getPrimaryCaret().moveToOffset(context.getStartOffset() + newCursorPos);
			}
		};

		WriteCommandAction.runWriteCommandAction(context.getProject(), runnable);
	}


}
