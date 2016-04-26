package com.kaylerrenslow.a3plugin.project;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.kaylerrenslow.a3plugin.lang.header.HeaderFileType;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;
import com.kaylerrenslow.a3plugin.lang.header.exception.DescriptionExtNotDefinedException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderFile;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.Stringtable;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.Collection;

/**
 * @author Kayler
 * Created on 03/31/2016.
 */
public class ArmaModuleData{

	private final Module module;
	private HeaderFile descriptionExt = null;
	private Stringtable stringtable;

	ArmaModuleData(@NotNull Module module) {
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
		VirtualFile descriptionExtVF = PluginUtil.findFileInModuleByName("description.ext", this.module, HeaderFileType.INSTANCE, true);
		if(descriptionExtVF == null){
			throw new DescriptionExtNotDefinedException();
		}
		this.descriptionExt = (HeaderFile) PsiManager.getInstance(this.module.getProject()).findFile(descriptionExtVF);
		return this.descriptionExt;
	}

	/** Get the stringtable.xml for this module
	 * @return Stringtable instance
	 * @throws FileNotFoundException if stringtable.xml doesn't exist
	 */
	public Stringtable getStringtable() throws FileNotFoundException{
		if(this.stringtable != null && this.stringtable.getVirtualFile().exists()){
			return this.stringtable;
		}
		this.stringtable = Stringtable.load(this.module);
		return this.stringtable;
	}
}
