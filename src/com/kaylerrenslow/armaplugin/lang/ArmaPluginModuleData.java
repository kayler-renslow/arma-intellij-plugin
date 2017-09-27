package com.kaylerrenslow.armaplugin.lang;

import com.intellij.openapi.module.Module;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunction;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunctionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 09/08/2017
 */
public class ArmaPluginModuleData {
	private HeaderFile rootConfigHeaderFile;
	private boolean reparseRootConfigHeaderFile = true;
	private final Module module;
	private List<HeaderConfigFunction> functions = null;

	public ArmaPluginModuleData(@NotNull Module module) {
		this.module = module;
	}

	boolean shouldReparseRootConfigHeaderFile() {
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
				functions = Collections.unmodifiableList(
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

	/**
	 * @return a read-only list of all config functions
	 */
	@Nullable
	public List<HeaderConfigFunction> getAllConfigFunctions() {
		return functions;
	}
}
