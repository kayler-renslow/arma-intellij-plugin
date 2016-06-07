package com.kaylerrenslow.a3plugin.util;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.util.indexing.FileBasedIndex;
import com.kaylerrenslow.a3plugin.wizards.ArmaModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 Created on 01/02/2016.
 */
public class PluginUtil {

	/**
	 Test to see if the given module is an Arma module

	 @param module module
	 @return true if the module is an Arma module, false otherwise
	 */
	public static boolean moduleIsArmaType(@Nullable Module module) {
		if (module == null) {
			return false;
		}
		return ModuleUtil.getModuleType(module) == ArmaModuleType.getInstance();
	}


	/**
	 Finds the file (specified by filePath) inside the given root directory. The search does not include the root directory itself. For instance, filePath could be equal to root directory ("exampleRootName" and return null)

	 @param filePath FilePath object
	 @param rootDirectory the root directory to beging the search
	 @return the VirtualFile that was found, or null if the given file path points to nothing at the given root directory
	 */
	@Nullable
	public static PsiFile findFileByPath(@NotNull FilePath filePath, @NotNull PsiDirectory rootDirectory, @NotNull Project project) {
		if (filePath.getFileName().matches("[a-zA-Z]+:")) { //starts with drive. example  d:/file/file2.txt
			while (rootDirectory.getParent() != null) {
				rootDirectory = rootDirectory.getParent();
			}
			filePath = filePath.getChild();
			if (filePath == null) {
				return null;
			}
		} else {
			while (filePath.fileNameIsDotDot()) {
				if (rootDirectory.getParent() == null) {
					return null;
				}
				rootDirectory = rootDirectory.getParent();
				filePath = filePath.getChild();
				if (filePath == null) { //just simply a ../
					return null;
				}
			}
		}
		PsiDirectory matched = rootDirectory.findSubdirectory(filePath.getFileName());

		if (matched != null) {
			if (filePath.getChild() == null) {
				return rootDirectory.findFile(filePath.getFileName());
			} else {
				return findFileByPath(filePath.getChild(), matched, project);
			}
		} else {
			if (filePath.getChild() == null) {
				return rootDirectory.findFile(filePath.getFileName());
			}
		}

		return null;
	}


	/**
	 Finds the first file with the given name in the given module

	 @param name file name to search for
	 @param module module
	 @param fileTypeInstance file type instance
	 @param ignoreCase true if the name of the file doesn't need to be matched by case, false if case matters
	 @return the found file, or null if none could be found
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

	@Nullable
	public static Module getModuleForPsiFile(PsiFile file) {
		final ProjectFileIndex index = ProjectRootManager.getInstance(file.getProject()).getFileIndex();

		if (file.getVirtualFile() == null) {
			file = file.getOriginalFile();
		}
		if (file.getVirtualFile() == null) {
			return null;
		}
		return index.getModuleForFile(file.getVirtualFile());
	}

}
