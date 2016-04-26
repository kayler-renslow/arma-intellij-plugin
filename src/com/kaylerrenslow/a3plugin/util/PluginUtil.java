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
import java.net.URL;
import java.util.Collection;

/**
 * Created on 01/02/2016.
 */
public class PluginUtil {

	public static File convertURLToFile(URL url) {
		File f;
		try {
			f = new File(url.toURI());
			f = new File(f.getPath());
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return null;
		}
		return f;
	}

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

	/** Finds the first instance where the given file path is met. If no file could be found, result is null.
	 * @param module module
	 * @param filePath file path to search for
	 * @return matched VirtualFile or null if no file could be matched with the given file path
	 */
	public static VirtualFile findFileInModuleByPath(@NotNull Module module, @NotNull FilePath filePath) {
		FilePathContentInterator iter = new FilePathContentInterator(module, filePath);
		FileBasedIndex.getInstance().iterateIndexableFiles(iter, module.getProject(), null);
		return iter.getMatchedFile();
	}

	private static class FilePathContentInterator implements ContentIterator{
		private final Module module;
		private FilePath root;
		private FilePath cursor;
		private VirtualFile matchedFile;
		private boolean foundRoot = false;

		/** Creates an iterator that looks for the given file path. Use getMatchedFile() to get the file matched with the given file path. The first path found will be the resulted virtual file.
		 * @param module module
		 * @param root root FilePath
		 */
		public FilePathContentInterator(@NotNull Module module, @NotNull FilePath root) {
			this.module = module;
			this.root = this.cursor = root;
		}

		@Override
		public boolean processFile(VirtualFile fileOrDir) {
			System.out.println(fileOrDir.getPath());
			System.out.println(module.getProject().getName());
			System.out.println(module.getName());
			boolean inModule = module.getModuleContentScope().contains(fileOrDir);
			if(!inModule || matchedFile != null){
				return false;
			}

			//suppose file path = test/test1
			//there may be many folders named test, so we need to verify that test1 is actually the child of test.

			if(fileOrDir.getName().equals(cursor.getName())){
				cursor = cursor.getChild();
				foundRoot = true;
			}else if(foundRoot){ //the root was found but the current file isn't inside the current path, so reset the search
				this.cursor = this.root;
				foundRoot = false;
			}

			if(cursor == null){
				matchedFile = fileOrDir;
			}
			return cursor == null; //only advance when the file path hasn't been found
		}

		@Nullable
		public VirtualFile getMatchedFile(){
			return this.matchedFile;
		}
	}

	public static class FilePath {
		@NotNull
		private final String name;

		@Nullable
		private FilePath child;

		public FilePath(@NotNull String name, @Nullable FilePath child) {
			this.name = name;
			this.child = child;
		}

		@NotNull
		public String getName() {
			return name;
		}

		@Nullable
		public FilePath getChild() {
			return child;
		}

		public static FilePath createFilePathFromNames(@NotNull String... fileNames) {
			if (fileNames.length == 0) {
				throw new IllegalArgumentException("must be at least 1 file name");
			}
			FilePath root = new FilePath(fileNames[0], null);
			FilePath cur = root;
			for (int i = 1; i < fileNames.length; i++) {
				cur.child = new FilePath(fileNames[i], null);
				cur = cur.child;
			}
			return root;
		}

		@Override
		public String toString() {
			return this.name + (this.child != null ? "/" + this.child.toString() : "");
		}
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
