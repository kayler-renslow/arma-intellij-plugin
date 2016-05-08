package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderConfigFunction;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.Stringtable;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.StringtableKey;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.providers.completionElements.SQFCompletionElementTextReplace.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.a3plugin.project.ArmaProjectDataManager;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * @author Kayler
 *         Does the backend work for SQF auto completion operations
 *         Created on 01/02/2016.
 */
public class SQFCompletionProvider extends CompletionProvider<CompletionParameters> {

	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		PsiElement cursor = parameters.getOriginalPosition(); //cursor is on a word

		boolean originalPositionNull = cursor == null;
		if (originalPositionNull) {
			cursor = parameters.getPosition(); //cursor is after a word
		}

		ASTNode prevSiblingNotWhitespace;

		if (PsiUtil.isOfElementType(cursor.getNode(), SQFTypes.LOCAL_VAR) || PsiUtil.isOfElementType(cursor.getNode(), SQFTypes.GLOBAL_VAR)) {
//			System.out.println("SQFCompletionProvider.addCompletions originalPositionNull:" + originalPositionNull);
			prevSiblingNotWhitespace = PsiUtil.getPrevSiblingNotWhitespace(cursor.getNode().getTreeParent()); //get parent because local_var and global_var is inside SQFTypes.VARIABLE
		} else {
//			System.out.println("SQFCompletionProvider.addCompletions originalPositionNull:" + originalPositionNull);
			prevSiblingNotWhitespace = PsiUtil.getPrevSiblingNotWhitespace(cursor.getNode());
		}

		String prevSibText;
		if (prevSiblingNotWhitespace == null) {
			prevSibText = "";
		} else {
			prevSibText = prevSiblingNotWhitespace.getText();
		}

		boolean isFunctionCallExp = prevSibText.equals("call") || prevSibText.equals("spawn");

		if (cursor.getText().contains("_fnc") || isFunctionCallExp) {
			if (!addBisFunctions(result, cursor)) {
				completeFunctionCall(parameters, result);
				addVariablesAndCommands(parameters, result, cursor, false);
			}
		} else if (prevSibText.equals("localize")) {
			completeLocalize(result, cursor);
		} else {
			completeCurrentWord(parameters, context, result, cursor);
		}

	}

	private void completeLocalize(@NotNull CompletionResultSet result, @NotNull PsiElement cursor) {
		Module module = PluginUtil.getModuleForPsiFile(cursor.getContainingFile());
		Stringtable table;
		try {
			table = ArmaProjectDataManager.getInstance().getDataForModule(module).getStringtable();
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.out);
			return;
		}
		StringtableKey[] keys = table.getAllKeysValues();
		for (StringtableKey key : keys) {
			result.addElement(key.getLookupElement(false));
		}
	}

	private void completeFunctionCall(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
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

	private void completeCurrentWord(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result, PsiElement cursor) {
		if (addBisFunctions(result, cursor)) {
			return; //adding anything else is a waste of computation at this point
		}

		result.addElement(new SQFCompInsertHandlerHintfln().getLookupElement(parameters, context, result));
		result.addElement(new SQFCompInsertHandlerHintfo().getLookupElement(parameters, context, result));
		result.addElement(new SQFCompInsertHandlerParams().getLookupElement(parameters, context, result));

		addVariablesAndCommands(parameters, result, cursor, true);

	}

	private void addVariablesAndCommands(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result, PsiElement cursor, boolean allowCommands) {
		Project project = cursor.getProject();
		boolean lookForLocalVars = cursor.getNode().getText().charAt(0) == '_';

		ArrayList<ASTNode> elements = new ArrayList<>();
		String localVarTailText;
		if (lookForLocalVars) { //find all local vars
			elements.addAll(PsiUtil.findDescendantElements(parameters.getOriginalFile(), SQFTypes.LOCAL_VAR, cursor.getNode()));
			localVarTailText = " " + Plugin.resources.getString("lang.sqf.completion.tail_text.local_variable");

			result.addElement(LookupElementBuilder.create("_this").withIcon(PluginIcons.ICON_SQF_MAGIC_VARIABLE).withTailText(" (magic variable)"));
			result.addElement(LookupElementBuilder.create("_x").withIcon(PluginIcons.ICON_SQF_MAGIC_VARIABLE).withTailText(" (magic variable)"));
		} else { //find all global vars
			elements.addAll(PsiUtil.findDescendantElements(parameters.getOriginalFile(), SQFTypes.GLOBAL_VAR, cursor.getNode()));
			localVarTailText = " " + Plugin.resources.getString("lang.sqf.completion.tail_text.global_variable");
		}

		for (ASTNode node : elements) { //add all variables
			if (!SQFStatic.followsSQFFunctionNameRules(node.getText())) {
				result.addElement(LookupElementBuilder.create(node.getText()).withTailText(localVarTailText).withIcon(PluginIcons.ICON_SQF_VARIABLE));
			}
		}

		if (!lookForLocalVars && allowCommands) { //add all commands
			String commandName;
			String trailText = Plugin.resources.getString("lang.sqf.completion.tail_text.command");
			for (int i = 0; i < SQFStatic.LIST_COMMANDS.size(); i++) {
				commandName = SQFStatic.LIST_COMMANDS.get(i);
				result.addElement(LookupElementBuilder.createWithSmartPointer(commandName, SQFPsiUtil.createElement(project, commandName, SQFTypes.COMMAND)).withIcon(PluginIcons.ICON_SQF_COMMAND).appendTailText(" " + trailText, true));
			}
		}
	}

	private boolean addBisFunctions(@NotNull CompletionResultSet result, PsiElement cursor) {
		Project project = cursor.getProject();
		if (SQFStatic.isMaybeBISFunction(cursor.getText())) { //add all bis functions
			String functionName;
			String tailText = Plugin.resources.getString("lang.sqf.completion.tail_text.bis_function");
			for (int i = 0; i < SQFStatic.LIST_BIS_FUNCTIONS.size(); i++) {
				functionName = SQFStatic.LIST_BIS_FUNCTIONS.get(i);
				result.addElement(LookupElementBuilder.createWithSmartPointer(functionName, SQFPsiUtil.createElement(project, functionName, SQFTypes.GLOBAL_VAR)).withIcon(PluginIcons.ICON_SQF_FUNCTION).appendTailText(" " + tailText, true));
			}
			return true;
		}
		return false;
	}
}
