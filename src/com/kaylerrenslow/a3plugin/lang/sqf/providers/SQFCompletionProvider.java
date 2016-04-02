package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.header.ConfigClassNotDefinedException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.impl.HeaderConfigFunction;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.providers.completionElements.SQFCompletionElementTextReplace;
import com.kaylerrenslow.a3plugin.lang.sqf.providers.completionElements.SQFCompletionElementTextReplace.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * @author Kayler
 *         Does the backend work for SQF auto completion operations
 *         Created on 01/02/2016.
 */
public class SQFCompletionProvider extends com.intellij.codeInsight.completion.CompletionProvider<CompletionParameters> {

	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		PsiElement cursor = parameters.getOriginalPosition(); //cursor is on a word

		if(cursor != null){
			completeCurrentWord(parameters, context, result, cursor);
			return;
		}

		cursor = parameters.getPosition(); //cursor is after a word

		if(!PsiUtil.isOfElementType(cursor.getNode(), SQFTypes.LOCAL_VAR) && !PsiUtil.isOfElementType(cursor.getNode(), SQFTypes.GLOBAL_VAR)){
			return;
		}
		ASTNode prevSiblingNotWhitespace = PsiUtil.getPrevSiblingNotWhitespace(cursor.getNode().getTreeParent()); //get parent because local_var and global_var is inside SQFTypes.VARIABLE
		if (PsiUtil.isOfElementType(prevSiblingNotWhitespace, SQFTypes.COMMAND) && (prevSiblingNotWhitespace.getText().equals("call") || prevSiblingNotWhitespace.getText().equals("spawn"))) {
			try {
				ArrayList<HeaderConfigFunction> configFunctions = HeaderPsiUtil.getAllConfigFunctionsFromDescriptionExt(parameters.getOriginalFile());
				String tailTextFormat = " " + Plugin.resources.getString("lang.sqf.completion.tail_text.function");
				for (HeaderConfigFunction configFunction : configFunctions) {
					result.addElement(LookupElementBuilder.create(configFunction, configFunction.getCallableName()).withTailText(String.format(tailTextFormat, configFunction.getFullRelativePath()), true).withIcon
							(HeaderConfigFunction.getIcon()));
				}
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}

	}

	private void completeCurrentWord(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result, PsiElement cursor) {
		Project project = parameters.getOriginalFile().getProject();

		if (cursor.getText().startsWith("BIS_")) {
			String functionName;
			String trailText = Plugin.resources.getString("lang.sqf.completion.tail_text.bis_function");
			for (int i = 0; i < SQFStatic.LIST_FUNCTIONS.size(); i++) {
				functionName = SQFStatic.LIST_FUNCTIONS.get(i);
				result.addElement(LookupElementBuilder.createWithSmartPointer(functionName, SQFPsiUtil.createElement(project, functionName, SQFTypes.GLOBAL_VAR)).withIcon(PluginIcons.ICON_SQF_FUNCTION).appendTailText(" " + trailText, true));
			}
			return; //adding anything else is a waste of computation at this point
		}

		boolean lookForLocalVars = cursor.getNode().getText().charAt(0) == '_';

		result.addElement(new SQFCompInsertHandlerHintfln().getLookupElement(parameters, context, result));
		result.addElement(new SQFCompInsertHandlerHintfo().getLookupElement(parameters, context, result));
		result.addElement(new SQFCompInsertHandlerHintArg().getLookupElement(parameters, context, result));
		result.addElement(new SQFCompInsertHandlerIfThen().getLookupElement(parameters, context, result));
		result.addElement(new SQFCompInsertHandlerIfExitWith().getLookupElement(parameters, context, result));

		ArrayList<ASTNode> elements = new ArrayList<>();
		if (lookForLocalVars) {
			elements.addAll(PsiUtil.findDescendantElements(parameters.getOriginalFile(), SQFTypes.LOCAL_VAR, cursor.getNode()));
			result.addElement(LookupElementBuilder.create("_this").withIcon(PluginIcons.ICON_SQF_MAGIC_VARIABLE).withTailText(" (magic variable)"));
			result.addElement(LookupElementBuilder.create("_x").withIcon(PluginIcons.ICON_SQF_MAGIC_VARIABLE).withTailText(" (magic variable)"));
		} else {
			elements.addAll(PsiUtil.findDescendantElements(parameters.getOriginalFile(), SQFTypes.GLOBAL_VAR, cursor.getNode()));
		}

		for (ASTNode node : elements) {
			if (!SQFPsiUtil.isBisFunction(node.getPsi())) {
				if (node.getPsi() instanceof PsiNamedElement) {
					result.addElement(LookupElementBuilder.create((PsiNamedElement) node.getPsi()).withIcon(PluginIcons.ICON_SQF_VARIABLE));
				} else {
					result.addElement(LookupElementBuilder.create(node.getText()).withIcon(PluginIcons.ICON_SQF_VARIABLE));
				}
			}
		}


		if (!lookForLocalVars) {
			String commandName;
			String trailText = Plugin.resources.getString("lang.sqf.completion.tail_text.command");
			for (int i = 0; i < SQFStatic.LIST_COMMANDS.size(); i++) {
				commandName = SQFStatic.LIST_COMMANDS.get(i);
				result.addElement(LookupElementBuilder.createWithSmartPointer(commandName, SQFPsiUtil.createElement(project, commandName, SQFTypes.COMMAND)).withIcon(PluginIcons.ICON_SQF_COMMAND).appendTailText(" " + trailText, true));
			}
		}
	}
}
