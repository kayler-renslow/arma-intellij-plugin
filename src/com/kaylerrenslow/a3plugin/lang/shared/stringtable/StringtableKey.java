package com.kaylerrenslow.a3plugin.lang.shared.stringtable;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.xml.XmlTag;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Container;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 *         Wrapper class for Key xml elements inside stringtable.xml
 *         Created on 04/09/2016.
 */
public class StringtableKey {
	private final Key key;
	private Container container;

	public StringtableKey(@NotNull Key key, @Nullable Container container) {
		this.key = key;
		this.container = container;
	}


	public String getKeyName() {
		return key.getID().getXmlAttribute().getValue();
	}

	private String getTailText() {
		if (this.container == null) {
			return this.key.getXmlElement().getContainingFile().getName();
		}
		return this.key.getXmlElement().getContainingFile().getName() + " - "+container.getName().getXmlAttribute().getValue();
	}

	/**
	 * Gets the keyname but replaces str_ with $STR_
	 *
	 * @return key name
	 */
	public String getDollarKeyName() {
		String find = "str_"; //str_ may be in all caps
		String keyName = "$" + getKeyName().replaceFirst(find, "STR_");
		return keyName;
	}

	/**
	 * Get a lookup element for this key
	 *
	 * @param isDollar true if the text to look for starts with $STR_, false otherwise. Also, when isDollar is false, the text should be surrounded with quotes
	 * @return new lookup element
	 */
	public LookupElement getLookupElement(boolean isDollar) {
		LookupElementBuilder element = LookupElementBuilder.create(new StringtableLookupElementDataObject(key.getXmlTag()), isDollar ? getDollarKeyName() : getKeyName()).withTailText(" (" + getTailText() + ")", true).withCaseSensitivity(false);
		if (!isDollar) {
			element = element.withInsertHandler(new TextReplace());
		}
		return element;
	}

	private class TextReplace implements InsertHandler<LookupElement> {

		@Override
		public void handleInsert(InsertionContext context, LookupElement item) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					context.getDocument().replaceString(context.getStartOffset(), context.getTailOffset(), "\"" + item.getLookupString() + "\"");
				}
			};

			WriteCommandAction.runWriteCommandAction(context.getProject(), runnable);
		}

	}
}
