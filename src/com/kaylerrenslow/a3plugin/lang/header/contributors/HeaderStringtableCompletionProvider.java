package com.kaylerrenslow.a3plugin.lang.header.contributors;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.module.Module;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.StringTable;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.StringtableKey;
import com.kaylerrenslow.a3plugin.project.ArmaProjectDataManager;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;

/**
 * Auto completion for $str_
 *
 * @author Kayler
 * @since 04/09/2016
 */
public class HeaderStringtableCompletionProvider extends CompletionProvider<CompletionParameters> {
	@Override
	protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
		StringTable table;
		Module module = PluginUtil.getModuleForPsiFile(parameters.getOriginalFile());
		try {
			table = ArmaProjectDataManager.getInstance().getDataForModule(module).getStringtable();
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.out);
			return;
		}
		StringtableKey[] keys = table.getAllKeysValues();
		for (StringtableKey key : keys) {
			result.addElement(key.getLookupElement(true));
		}
	}
}
