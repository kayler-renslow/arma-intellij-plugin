package com.kaylerrenslow.armaplugin.lang;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
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
	@Nullable
	public static VirtualFile getDescriptionExtVirtualFile(@NotNull Project project, @NotNull PsiElement element) {
		ProjectFileIndex index = ProjectRootManager.getInstance(project).getFileIndex();
		PsiFile psiFile = element.getContainingFile();
		if (psiFile == null) {
			return null;
		}
		Module module = index.getModuleForFile(psiFile.getVirtualFile(), false);
		if (module == null) {
			return null;
		}
		Collection<VirtualFile> files = FileTypeIndex.getFiles(SQFFileType.INSTANCE, module.getModuleContentScope());
		for (VirtualFile virtFile : files) {
			if (virtFile.getName().equalsIgnoreCase("description.ext")) {
				return virtFile;
			}
		}
		return null;
	}
}
