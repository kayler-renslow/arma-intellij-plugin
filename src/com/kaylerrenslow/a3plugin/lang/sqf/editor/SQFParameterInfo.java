package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

/**
 * Created by Kayler on 04/15/2016.
 */
public class SQFParameterInfo implements ParameterInfoHandler {
	@Override
	public boolean couldShowInLookup() {
		System.out.println("SQFParameterInfo.couldShowInLookup");
		return false;
	}

	@Nullable
	@Override
	public Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
		System.out.println("SQFParameterInfo.getParametersForLookup");
		return new Object[0];
	}

	@Nullable
	@Override
	public Object[] getParametersForDocumentation(Object p, ParameterInfoContext context) {
		System.out.println("SQFParameterInfo.getParametersForDocumentation");
		return new Object[0];
	}

	@Nullable
	@Override
	public Object findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
		System.out.println("SQFParameterInfo.findElementForParameterInfo");
		PsiElement psiElement = context.getFile().findElementAt(context.getOffset());
		return psiElement;
	}

	@Override
	public void showParameterInfo(@NotNull Object element, @NotNull CreateParameterInfoContext context) {
		System.out.println("SQFParameterInfo.showParameterInfo");
		context.setItemsToShow(new Object[]{element});

		context.showHint((PsiElement)element, ((PsiElement) element).getTextOffset(), this);
	}

	@Nullable
	@Override
	public Object findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
		System.out.println("SQFParameterInfo.findElementForUpdatingParameterInfo");
		return context.getObjectsToView()[0];
	}

	@Override
	public void updateParameterInfo(@NotNull Object o, @NotNull UpdateParameterInfoContext context) {
		System.out.println("SQFParameterInfo.updateParameterInfo");
		context.setCurrentParameter(0);
	}

	@Nullable
	@Override
	public String getParameterCloseChars() {
		System.out.println("SQFParameterInfo.getParameterCloseChars");
		return null;
	}

	@Override
	public boolean tracksParameterIndex() {
		System.out.println("SQFParameterInfo.tracksParameterIndex");
		return false;
	}

	@Override
	public void updateUI(Object p, @NotNull ParameterInfoUIContext context) {
		System.out.println("SQFParameterInfo.updateUI");
		PsiElement element = (PsiElement)p;
		int index = Collections.binarySearch(SQFStatic.LIST_COMMANDS, element.getText());
		if(index < 0){
			return;
		}
		String commandParams = SQFStatic.getCommandDocSyntax(SQFStatic.LIST_COMMANDS.get(index));
		if(commandParams == null){
			return;
		}
		context.setupUIComponentPresentation(commandParams, 0, 0, false, false, false, JBColor.background());
		context.setUIComponentEnabled(true);
		//		context.setUIComponentEnabled(true);
	}
}
