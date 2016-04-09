package com.kaylerrenslow.a3plugin.project;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.kaylerrenslow.a3plugin.lang.header.HeaderFileType;
import com.kaylerrenslow.a3plugin.lang.header.exception.DescriptionExtNotDefinedException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderFile;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.Collection;

/**
 * Created by Kayler on 03/31/2016.
 */
public class ArmaModuleData{

	private final Module module;
	private HeaderFile descriptionExt = null;

	public ArmaModuleData(@NotNull Module module) {
		this.module = module;
	}

	public Module getModule() {
		return module;
	}

	/**
	 * Get a HeaderFile instance that points to the mission's description.ext
	 *
	 * @return description.ext as HeaderFile
	 * @throws DescriptionExtNotDefinedException when description.ext doesn't exist
	 */
	public HeaderFile getDescriptionExt() throws DescriptionExtNotDefinedException {
		if(descriptionExt != null && descriptionExt.getVirtualFile().exists()){
			return descriptionExt;
		}
		Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, HeaderFileType.INSTANCE, this.module.getModuleContentScope());
		for (VirtualFile file : files) {
			if (file.getName().equalsIgnoreCase("description.ext")) {
				this.descriptionExt = (HeaderFile) PsiManager.getInstance(this.module.getProject()).findFile(file);
				return this.descriptionExt;
			}
		}
		throw new DescriptionExtNotDefinedException();

	}
}
