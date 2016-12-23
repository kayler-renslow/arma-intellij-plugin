package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.shared.completion.CompletionElementWithTextReplace;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * CompletionElementWithTextReplace extension points for SQF language.
 * Created on 03/28/2016.
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
	 * Auto completion for hintarg.
	 * Created on 03/31/2016.
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
			return LookupElementBuilder.create("hintln").withInsertHandler(this).withTypeText(" hint current file and line number");
		}
	}


	public static class SQFCompInsertHandlerParams extends CompletionElementWithTextReplace{
		public SQFCompInsertHandlerParams() {
			super("params", "params[\"\"];", 8, "set up a params[] statement");
		}
	}

}
