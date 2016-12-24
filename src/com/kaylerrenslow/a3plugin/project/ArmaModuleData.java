package com.kaylerrenslow.a3plugin.project;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.kaylerrenslow.a3plugin.lang.header.HeaderFileType;
import com.kaylerrenslow.a3plugin.lang.header.exception.DescriptionExtNotDefinedException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderFile;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.StringTable;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;

/**
 * @author Kayler
 * @since 03/31/2016
 */
public class ArmaModuleData {

	private final Module module;
	private HeaderFile descriptionExt = null;
	private StringTable stringtable;
	private VirtualFile descriptionExtVF;

	ArmaModuleData(@NotNull Module module) {
		this.module = module;
	}

	@NotNull
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
		if (descriptionExt != null && descriptionExt.getVirtualFile().exists()) {
			return descriptionExt;
		}
		this.descriptionExtVF = PluginUtil.findFileInModuleByName("description.ext", this.module, HeaderFileType.INSTANCE, true);

		if (descriptionExtVF == null) {
			throw new DescriptionExtNotDefinedException();
		}
		this.descriptionExt = (HeaderFile) PsiManager.getInstance(this.module.getProject()).findFile(descriptionExtVF);
		return this.descriptionExt;
	}

	/**
	 * Get's the root mission directory file (description.ext's parent directory)
	 *
	 * @return directory, or null if the description.ext isn't defined
	 */
	@NotNull
	public PsiDirectory getRootMissionDirectory() throws DescriptionExtNotDefinedException {
		getDescriptionExt();
		return this.descriptionExt.getContainingDirectory();
	}


	/**
	 * Get the stringtable.xml for this module
	 *
	 * @return StringTable instance
	 * @throws FileNotFoundException if stringtable.xml doesn't exist
	 */
	@NotNull
	public StringTable getStringtable() throws FileNotFoundException {
		if (this.stringtable != null && this.stringtable.getVirtualFile().exists()) {
			return this.stringtable;
		}
		this.stringtable = StringTable.load(this.module);
		if (!stringtable.getVirtualFile().exists()) {
			throw new FileNotFoundException("stringtable.xml doesn't exist");
		}
		return this.stringtable;
	}
}
