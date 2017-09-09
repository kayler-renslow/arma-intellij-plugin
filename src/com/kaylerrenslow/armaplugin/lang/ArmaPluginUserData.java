package com.kaylerrenslow.armaplugin.lang;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderParser;
import com.kaylerrenslow.armaDialogCreator.util.ReadOnlyList;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
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
	 * Get the {@link HeaderFile} instance.
	 * Note: this method will reparse only if it is unset or out of date (file was updated).
	 *
	 * @return the root config file. This will be either description.ext (for missions) or config.cpp (for addons/mods)
	 */
	@Nullable
	public HeaderFile parseAndGetRootConfigHeaderFile(@NotNull PsiElement elementFromModule) {
		synchronized (this) {
			ArmaPluginModuleData moduleData = getModuleData(elementFromModule);
			if (moduleData == null) {
				return null;
			}
			if (!moduleData.reparseRootConfigHeaderFile() && moduleData.getRootConfigHeaderFile() != null) {
				return moduleData.getRootConfigHeaderFile();
			}

			//find a place to save parse data
			VirtualFile imlVirtFile = moduleData.getModule().getModuleFile();
			if (imlVirtFile == null) {
				return null;
			}
			VirtualFile imlDir = imlVirtFile.getParent();
			if (imlDir == null) {
				return null;
			}
			VirtualFile rootConfigVirtualFile = PluginUtil.getRootConfigVirtualFile(elementFromModule);
			if (rootConfigVirtualFile == null) {
				return null;
			}

			//parse the root config
			try {
				moduleData.setRootConfigHeaderFile(HeaderParser.parse(new File(rootConfigVirtualFile.getPath()), new File(imlDir.getPath() + "/armaplugin-temp")));
				moduleData.setReparseRootConfigHeaderFile(false);
			} catch (Exception e) {
				System.out.println("Header Parse Exception:" + e.getMessage());
				return null;
			}
			return moduleData.getRootConfigHeaderFile();
		}
	}

	@Nullable
	public ReadOnlyList<HeaderConfigFunction> getAllConfigFunctions(@NotNull PsiElement elementFromModule) {
		synchronized (this) {
			ArmaPluginModuleData moduleData = getModuleData(elementFromModule);
			if (moduleData == null) {
				return null;
			}
			if (moduleData.getRootConfigHeaderFile() == null) {
				parseAndGetRootConfigHeaderFile(elementFromModule);
			}
			return moduleData.getAllConfigFunctions();
		}
	}

	@Nullable
	private ArmaPluginModuleData getModuleData(@NotNull PsiElement elementFromModule) {
		Module module = ModuleUtil.findModuleForPsiElement(elementFromModule);
		if (module == null) {
			return null;
		}
		return moduleMap.computeIfAbsent(module, module1 -> {
			return new ArmaPluginModuleData(module);
		});
	}

	/**
	 * Invoke when the root config file ({@link #parseAndGetRootConfigHeaderFile(PsiElement)}) has been edited.
	 * Note that this doesn't do any reparsing and instead tells {@link #parseAndGetRootConfigHeaderFile(PsiElement)} that it's cached
	 * {@link HeaderFile} is no longer valid and it should reparse.
	 */
	void reparseRootConfig(@NotNull PsiFile fileFromModule) {
		synchronized (this) {
			Module module = ModuleUtil.findModuleForPsiElement(fileFromModule);
			if (module == null) {
				return;
			}
			ArmaPluginModuleData moduleData = moduleMap.computeIfAbsent(module, module1 -> {
				return new ArmaPluginModuleData(module);
			});
			moduleData.setReparseRootConfigHeaderFile(true);
		}
	}

}
