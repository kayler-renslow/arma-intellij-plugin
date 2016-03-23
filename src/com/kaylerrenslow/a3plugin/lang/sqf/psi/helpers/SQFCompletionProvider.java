package com.kaylerrenslow.a3plugin.lang.sqf.psi.helpers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Kayler
 * Does the backend work for SQF auto completion operations
 * Created on 01/02/2016.
 */
public class SQFCompletionProvider extends com.intellij.codeInsight.completion.CompletionProvider<CompletionParameters>{

	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		PsiElement cursor = parameters.getOriginalPosition();

		boolean lookForLocalVars = cursor.getNode().getText().charAt(0) == '_';

		ArrayList<ASTNode> elements = new ArrayList<>();
		if(lookForLocalVars){
			elements.addAll(PsiUtil.findElements(parameters.getOriginalFile(), SQFTypes.LOCAL_VAR, cursor.getNode()));
		}else{
			elements.addAll(PsiUtil.findElements(parameters.getOriginalFile(), SQFTypes.GLOBAL_VAR, cursor.getNode()));
		}


		for(ASTNode node : elements){
			if(node.getPsi() instanceof PsiNamedElement){
				result.addElement(LookupElementBuilder.create((PsiNamedElement)node.getPsi()).withIcon(PluginIcons.ICON_SQF_VARIABLE));
			}else{
				result.addElement(LookupElementBuilder.create(node.getText()).withIcon(PluginIcons.ICON_SQF_VARIABLE));
			}
		}
		if(!lookForLocalVars){
			for(int i = 0; i < SQFStatic.LIST_COMMANDS.size(); i++){
				result.addElement(LookupElementBuilder.create(SQFStatic.LIST_COMMANDS.get(i)).withIcon(PluginIcons.ICON_SQF_COMMAND));
			}
		}
	}
}
