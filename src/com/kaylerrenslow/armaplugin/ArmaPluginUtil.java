package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.FileTypeIndex;
import com.kaylerrenslow.armaplugin.lang.header.HeaderFileType;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 09/07/2017
 */
public class ArmaPluginUtil {
	/**
	 * Gets the config files (either a description.ext or multiple, at least 1, config.cpp (case sensitivity doesn't matter)).
	 * If no description.ext file or config.cpp files could be found, this will return an empty list. This will also
	 * return and empty list if a module for the given PsiElement couldn't be found.
	 * <p>
	 * If a description.ext file is found, this method will return a singleton list of the description.ext file,regardless
	 * if there are config.cpp files. If there is no description.ext files, this will return all config.cpp files found
	 *
	 * @param elementFromModule a PsiElement used to determine what module the root config file is located in
	 * @return a list of VirtualFile instances, or an empty list
	 */
	@NotNull
	public static List<VirtualFile> getConfigVirtualFiles(@NotNull PsiElement elementFromModule) {
		Module module = ModuleUtil.findModuleForPsiElement(elementFromModule);
		if (module == null) {
			return Collections.emptyList();
		}
		Collection<VirtualFile> files = FileTypeIndex.getFiles(HeaderFileType.INSTANCE, module.getModuleContentScope());
		List<VirtualFile> configs = new ArrayList<>();
		for (VirtualFile virtFile : files) {
			if (virtFile.getName().equalsIgnoreCase("description.ext")) {
				return Collections.singletonList(virtFile);
			}
			if (virtFile.getName().equalsIgnoreCase("config.cpp")) {
				configs.add(virtFile);
			}

		}
		return configs;
	}

	@NotNull
	public static String getExceptionString(@NotNull Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}
}
