package com.kaylerrenslow.a3plugin.lang.sqf.providers.completionElements;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.shared.completion.CompletionElementWithTextReplace;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Kayler on 03/28/2016.
 */
public class SQFCompletionElementTextReplace{
	/**
	 * Auto completion for hintfo.
	 * Created on 03/28/2016.
	 */
	public static class SQFCompInsertHandlerHintfo extends CompletionElementWithTextReplace{
		public SQFCompInsertHandlerHintfo() {
			super("hintfo", "hint format[\"\"];", 13, " hint format[]");
		}
	}

	/**
	 * Auto completion for if then statement.
	 * Created on 03/28/2016.
	 */
	public static class SQFCompInsertHandlerIfThen extends CompletionElementWithTextReplace{
		public SQFCompInsertHandlerIfThen() {
			super("if", "if () then {\n};", 4, " if then statement");
		}

		@Override
		public void handleInsert(InsertionContext context, LookupElement item) {
			super.handleInsert(context, item);
		}
	}

	/**
	 * Auto completion for if exitWith statement.
	 * Created on 03/28/2016.
	 */
	public static class SQFCompInsertHandlerIfExitWith extends CompletionElementWithTextReplace{
		public SQFCompInsertHandlerIfExitWith() {
			super("ife", "if () exitWith {};", 4, " if exitWith statement");
		}

		@Override
		public void handleInsert(InsertionContext context, LookupElement item) {
			super.handleInsert(context, item);
//			context.getEditor().
		}
	}

	/**
	 * Auto completion for hintfln.
	 * Created on 03/28/2016.
	 */
	public static class SQFCompInsertHandlerHintfln extends CompletionElementWithTextReplace{

		@Override
		public void handleInsert(InsertionContext context, LookupElement item) {
			String file = context.getFile().getParent().getName() + "/" + context.getFile().getName();
			int lineNum = context.getDocument().getLineNumber(context.getStartOffset()) + 1; //line postion starts from 0
			this.replaceStr = "hint \"" + file + " " + lineNum + "\";";
			this.newCursorPos = this.replaceStr.length();
			super.handleInsert(context, item);
		}

		@Override
		public LookupElement getLookupElement(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
			return LookupElementBuilder.create("hintfln").withInsertHandler(this).withTypeText(" hint current file and line number");
		}
	}

}
