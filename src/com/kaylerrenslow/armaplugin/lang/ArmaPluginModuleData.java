package com.kaylerrenslow.armaplugin.lang;

import com.intellij.openapi.module.Module;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaDialogCreator.util.ReadOnlyList;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunction;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunctionUtil;
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
	private ReadOnlyList<HeaderConfigFunction> functions = null;

	public ArmaPluginModuleData(@NotNull Module module) {
		this.module = module;
	}

	boolean reparseRootConfigHeaderFile() {
		return reparseRootConfigHeaderFile;
	}

	void setReparseRootConfigHeaderFile(boolean reparseRootConfigHeaderFile) {
		this.reparseRootConfigHeaderFile = reparseRootConfigHeaderFile;
		if (reparseRootConfigHeaderFile) {
			this.functions = null;
		}
	}

	void setRootConfigHeaderFile(@Nullable HeaderFile rootConfigHeaderFile) {
		this.rootConfigHeaderFile = rootConfigHeaderFile;
		if (rootConfigHeaderFile != null) {
			try {
				functions = new ReadOnlyList<>(
						HeaderConfigFunctionUtil.getAllConfigFunctionsFromRootConfig(HeaderConfigFunctionUtil.getCfgFunctions(rootConfigHeaderFile))
				);
			} catch (Exception ignore) {
				ignore.printStackTrace();
			}
		}
	}

	@Nullable
	public HeaderFile getRootConfigHeaderFile() {
		return rootConfigHeaderFile;
	}

	@NotNull
	public Module getModule() {
		return module;
	}

	@Nullable
	public ReadOnlyList<HeaderConfigFunction> getAllConfigFunctions() {
		return functions;
	}
}
