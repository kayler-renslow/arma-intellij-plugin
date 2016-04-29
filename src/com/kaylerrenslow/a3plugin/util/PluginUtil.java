package com.kaylerrenslow.a3plugin.util;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Created on 01/02/2016.
 */
public class PluginUtil {


	/**
	 * Finds the filePath inside the given module
	 *
	 * @param filePath         file path to find
	 * @param module           module
	 * @param fileTypeInstance file type
	 * @return the found file, or null if none could be found
	 */
	public static VirtualFile findFileInModule(@NotNull String filePath, Module module, @NotNull final LanguageFileType fileTypeInstance) {
		Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, fileTypeInstance, module.getModuleContentScope());
		for (VirtualFile file : files) {
			if (file.getPath().endsWith(filePath)) {
				return file;
			}
		}
		return null;
	}


	/**
	 * Finds a file by the given name in the given module
	 *
	 * @param name             file name to search for
	 * @param module           module
	 * @param fileTypeInstance file type instance
	 * @param ignoreCase       true if the name of the file doesn't need to be matched by case, false if case matters
	 * @return the found file, or null if none could be found
	 */
	public static VirtualFile findFileInModuleByName(@NotNull String name, Module module, @NotNull final LanguageFileType fileTypeInstance, boolean ignoreCase) {
		Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, fileTypeInstance, module.getModuleContentScope());
		for (VirtualFile file : files) {
			if ((file.getName().equalsIgnoreCase(name) && ignoreCase) || file.getName().equals(name)) {
				return file;
			}
		}
		return null;
	}

	@NotNull
	public static Module getModuleForPsiFile(PsiFile file) {
		final ProjectFileIndex index = ProjectRootManager.getInstance(file.getProject()).getFileIndex();

		if (file.getVirtualFile() == null) {
			file = file.getOriginalFile();
			//			return null;
		}
		final Module module = index.getModuleForFile(file.getVirtualFile());
		return module;
	}

}
