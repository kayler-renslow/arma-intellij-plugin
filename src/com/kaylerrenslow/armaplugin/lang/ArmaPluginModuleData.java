package com.kaylerrenslow.armaplugin.lang;

import com.intellij.openapi.module.Module;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * @since 09/08/2017
 */
public class ArmaPluginModuleData {
	private HeaderFile rootConfigHeaderFile;
	private boolean reparseRootConfigHeaderFile = true;
	private final Module module;

	public ArmaPluginModuleData(@NotNull Module module) {
		this.module = module;
	}

	boolean reparseRootConfigHeaderFile() {
		return reparseRootConfigHeaderFile;
	}

	void setReparseRootConfigHeaderFile(boolean reparseRootConfigHeaderFile) {
		this.reparseRootConfigHeaderFile = reparseRootConfigHeaderFile;
	}

	void setRootConfigHeaderFile(@Nullable HeaderFile rootConfigHeaderFile) {
		this.rootConfigHeaderFile = rootConfigHeaderFile;
	}

	@Nullable
	public HeaderFile getRootConfigHeaderFile() {
		return rootConfigHeaderFile;
	}

	@NotNull
	public Module getModule() {
		return module;
	}
}
