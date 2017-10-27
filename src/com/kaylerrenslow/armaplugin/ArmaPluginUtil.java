package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.FileTypeIndex;
import com.kaylerrenslow.armaplugin.lang.header.HeaderFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

/**
 * @author Kayler
 * @since 09/07/2017
 */
public class ArmaPluginUtil {
	/**
	 * Gets the root config file (either description.ext or config.cpp (case sensitivity doesn't matter)), or null if neither could be found
	 *
	 * @param elementFromModule a PsiElement used to determine what module the root config file is located in
	 * @return the VirtualFile instance, or null if the root config file couldn't be found
	 */
	@Nullable
	public static VirtualFile getRootConfigVirtualFile(@NotNull PsiElement elementFromModule) {
		Module module = ModuleUtil.findModuleForPsiElement(elementFromModule);
		if (module == null) {
			return null;
		}
		Collection<VirtualFile> files = FileTypeIndex.getFiles(HeaderFileType.INSTANCE, module.getModuleContentScope());
		for (VirtualFile virtFile : files) {
			if (virtFile.getName().equalsIgnoreCase("description.ext")) {
				return virtFile;
			}
			if (virtFile.getName().equalsIgnoreCase("config.cpp")) {
				return virtFile;
			}

		}
		return null;
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
