package com.kaylerrenslow.armaplugin.lang;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.FileTypeIndex;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author Kayler
 * @since 09/07/2017
 */
public class PluginUtil {
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
		Collection<VirtualFile> files = FileTypeIndex.getFiles(SQFFileType.INSTANCE, module.getModuleContentScope());
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
}
