package com.kaylerrenslow.armaplugin.lang.sqf.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.kaylerrenslow.armaplugin.ArmaPluginIcons;
import com.kaylerrenslow.armaplugin.ArmaPluginUserData;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunction;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFCommand;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @since 12/10/2017
 */
public class CompletionAdders {
	/**
	 * Adds all description.ext/config.cpp functions to the completion result
	 */
	static void addFunctions(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
		List<HeaderConfigFunction> allConfigFunctions = ArmaPluginUserData.getInstance().getAllConfigFunctions(parameters.getOriginalFile());
		if (allConfigFunctions == null) {
			return;
		}
		for (HeaderConfigFunction function : allConfigFunctions) {
			result.addElement(LookupElementBuilder.create(function)
					.withIcon(HeaderConfigFunction.getIcon())
					.withPresentableText(function.getCallableName())
			);
		}
	}

	/**
	 * Adds all SQF commands to the completion result
	 */
	static void addCommands(@NotNull Project project, @NotNull CompletionResultSet result) {
		for (String command : SQFStatic.LIST_COMMANDS) {
			SQFCommand cmd = PsiUtil.createElement(project, command, SQFFileType.INSTANCE, SQFCommand.class);
			if (cmd == null) {
				continue;
			}
			result.addElement(LookupElementBuilder.createWithSmartPointer(command, cmd)
					.withIcon(ArmaPluginIcons.ICON_SQF_COMMAND)
					.appendTailText(" (Command)", true)
			);
		}
	}

	/**
	 * Adds all SQF BIS functions to the result
	 */
	static void addBISFunctions(@NotNull Project project, @NotNull CompletionResultSet result) {
		for (String functionName : SQFStatic.LIST_BIS_FUNCTIONS) {
			SQFVariable fnc = PsiUtil.createElement(project, functionName, SQFFileType.INSTANCE, SQFVariable.class);
			if (fnc == null) {
				continue;
			}
			result.addElement(LookupElementBuilder.createWithSmartPointer(functionName, fnc)
					.withIcon(ArmaPluginIcons.ICON_SQF_FUNCTION)
					.appendTailText(" Bohemia Interactive Function", true)
			);
		}
	}
}

