package com.kaylerrenslow.armaplugin.lang.sqf.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.kaylerrenslow.armaplugin.ArmaPluginUserData;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunction;
import com.kaylerrenslow.armaplugin.stringtable.Key;
import com.kaylerrenslow.armaplugin.stringtable.StringTableProject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @since 09/09/2017
 */
public class SQFFunctionNameCompletionProvider extends CompletionProvider<CompletionParameters> {
	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		List<HeaderConfigFunction> allConfigFunctions = ArmaPluginUserData.getInstance().getAllConfigFunctions(parameters.getOriginalFile());
		if (allConfigFunctions == null) {
			return;
		}
		{ //testing code
			Project p = parameters.getOriginalFile().getProject();
			XmlFile stringTableXml = ArmaPluginUserData.getInstance().getStringTableXml(parameters.getOriginalFile());
			System.out.println("SQFFunctionNameCompletionProvider.addCompletions stringTableXml=" + stringTableXml);
			if (stringTableXml != null) {
				DomFileElement<StringTableProject> domFileElement = DomManager.getDomManager(p).getFileElement(stringTableXml, StringTableProject.class);
				System.out.println("SQFFunctionNameCompletionProvider.addCompletions domFileElement=" + domFileElement);
				if (domFileElement != null) {
					StringTableProject stringTableProject = domFileElement.getRootElement();
					List<Key> allKeys = stringTableProject.getAllKeys();
					System.out.println("SQFFunctionNameCompletionProvider.addCompletions allKeys=" + allKeys);
				}
			}
		}
		for (HeaderConfigFunction function : allConfigFunctions) {
			result.addElement(LookupElementBuilder.create(function).withIcon(HeaderConfigFunction.getIcon()).withPresentableText(function.getCallableName()));
		}
	}
}
