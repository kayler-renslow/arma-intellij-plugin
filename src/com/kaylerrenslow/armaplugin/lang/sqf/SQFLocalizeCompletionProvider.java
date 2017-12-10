package com.kaylerrenslow.armaplugin.lang.sqf;

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
import com.kaylerrenslow.armaplugin.stringtable.StringTableKey;
import com.kaylerrenslow.armaplugin.stringtable.StringTableProject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Completion contributor for localize "str_tag_example"
 *
 * @author Kayler
 * @since 12/10/2017
 */
public class SQFLocalizeCompletionProvider extends CompletionProvider<CompletionParameters> {
	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		Project p = parameters.getOriginalFile().getProject();
		XmlFile stringTableXml = ArmaPluginUserData.getInstance().getStringTableXml(parameters.getOriginalFile());
		if (stringTableXml != null) {
			DomFileElement<StringTableProject> domFileElement = DomManager.getDomManager(p).getFileElement(stringTableXml, StringTableProject.class);
			if (domFileElement != null) {
				StringTableProject stringTableProject = domFileElement.getRootElement();
				List<StringTableKey> allKeys = stringTableProject.getAllKeys();
				for (StringTableKey key : allKeys) {
					result.addElement(LookupElementBuilder.create(key.getKey(), key.getID()).appendTailText(" " + key.getContainerPath(), true));
				}
			}
		}
	}
}
