package com.kaylerrenslow.armaplugin.lang;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderParseException;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author Kayler
 * @since 09/08/2017
 */
public class ArmaPluginUserData {
	private static final ArmaPluginUserData instance = new ArmaPluginUserData();

	@NotNull
	public static ArmaPluginUserData getInstance() {
		return instance;
	}

	private final Map<Module, ArmaPluginModuleData> moduleMap = new IdentityHashMap<>();

	/**
	 * @return the root config file. This will be either description.ext (for missions) or config.cpp (for addons/mods)
	 */
	@Nullable
	public HeaderFile getRootConfigHeaderFile(@NotNull PsiFile fileFromModule) {
		synchronized (this) {
			Module module = ModuleUtil.findModuleForPsiElement(fileFromModule);
			if (module == null) {
				return null;
			}
			ArmaPluginModuleData moduleData = moduleMap.computeIfAbsent(module, module1 -> {
				return new ArmaPluginModuleData();
			});

			if (!moduleData.reparseRootConfigHeaderFile && moduleData.rootConfigHeaderFile != null) {
				return moduleData.rootConfigHeaderFile;
			}

			//find a place to save parse data
			VirtualFile imlVirtFile = module.getModuleFile();
			if (imlVirtFile == null) {
				return null;
			}
			VirtualFile imlDir = imlVirtFile.getParent();
			if (imlDir == null) {
				return null;
			}
			VirtualFile rootConfigVirtualFile = PluginUtil.getRootConfigVirtualFile(fileFromModule);
			if (rootConfigVirtualFile == null) {
				return null;
			}

			//parse the root config
			try {
				moduleData.rootConfigHeaderFile = HeaderParser.parse(new File(rootConfigVirtualFile.getPath()), new File(imlDir.getPath() + "/armaplugin-temp"));
				moduleData.reparseRootConfigHeaderFile = false;
			} catch (IOException e) {
				return null;
			} catch (HeaderParseException e) {
				System.out.println("HeaderParseException:" + e.getMessage());
				return null;
			}
			return moduleData.rootConfigHeaderFile;
		}
	}

	/**
	 * Invoke when the root config file ({@link #getRootConfigHeaderFile(Project, PsiFile)})
	 */
	void reparseRootConfig(@NotNull PsiFile fileFromModule) {
		synchronized (this) {
			Module module = ModuleUtil.findModuleForPsiElement(fileFromModule);
			if (module == null) {
				return;
			}
			ArmaPluginModuleData moduleData = moduleMap.computeIfAbsent(module, module1 -> {
				return new ArmaPluginModuleData();
			});
			moduleData.reparseRootConfigHeaderFile = true;
		}
	}

	private static class ArmaPluginModuleData {
		private HeaderFile rootConfigHeaderFile;
		private boolean reparseRootConfigHeaderFile = true;
	}

}
