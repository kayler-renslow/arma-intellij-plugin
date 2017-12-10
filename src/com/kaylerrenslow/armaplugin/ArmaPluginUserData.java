package com.kaylerrenslow.armaplugin;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderParser;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunction;
import com.kaylerrenslow.armaplugin.settings.ArmaPluginApplicationSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

/**
 * Used for storing data collected from the current IntelliJ process's lifetime.
 * This data will not be persisted after IntelliJ restarts.
 *
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
	 * Get a list of {@link HeaderFile} instances. It will be either a single description.ext (for missions)
	 * or multiple config.cpp (for addons/mods).
	 * Note: this method will reparse only if it is unset or out of date (file was updated).
	 *
	 * @return the root config file, or an empty list if couldn't locate a {@link Module} or couldn't be parsed
	 */
	@NotNull
	public List<HeaderFile> parseAndGetConfigHeaderFiles(@NotNull PsiElement elementFromModule) {
		synchronized (this) {
			ArmaPluginModuleData moduleData = getModuleData(elementFromModule);
			if (moduleData == null) {
				return Collections.emptyList();
			}
			if (!moduleData.shouldReparseConfigHeaderFiles() && moduleData.getConfigHeaderFiles() != null) {
				return moduleData.getConfigHeaderFiles();
			}

			//find a place to save parse data
			String imlDir = ArmaPlugin.getPathToTempDirectory(moduleData.getModule());
			if (imlDir == null) {
				return Collections.emptyList();
			}
			List<VirtualFile> configVirtualFiles = ArmaPluginUtil.getConfigVirtualFiles(elementFromModule);
			if (configVirtualFiles.isEmpty()) {
				return Collections.emptyList();
			}

			List<HeaderFile> parsedFiles = new ArrayList<>();
			for (VirtualFile configVirtualFile : configVirtualFiles) {
				try {
					HeaderFile file = HeaderParser.parse(
							new VirtualFileHeaderFileTextProvider(configVirtualFile, elementFromModule.getProject()),
							new File(imlDir)
					);
					parsedFiles.add(file);
				} catch (Exception e) {
					System.out.println("Header Parse Exception:" + e.getMessage());
					Notifications.Bus.notify(new HeaderFileParseErrorNotification(e));
				}
			}

			moduleData.setConfigHeaderFiles(parsedFiles);
			moduleData.setReparseConfigHeaderFiles(false);

			return moduleData.getConfigHeaderFiles();
		}
	}

	@Nullable
	public List<HeaderConfigFunction> getAllConfigFunctions(@NotNull PsiElement elementFromModule) {
		synchronized (this) {
			ArmaPluginModuleData moduleData = getModuleData(elementFromModule);
			if (moduleData == null) {
				return null;
			}
			if (moduleData.getConfigHeaderFiles().isEmpty() || moduleData.shouldReparseConfigHeaderFiles()) {
				parseAndGetConfigHeaderFiles(elementFromModule);
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
	 * Invoke when the root config file ({@link #parseAndGetConfigHeaderFiles(PsiElement)}) or included files for it has been edited.
	 * Note that this doesn't do any reparsing and instead tells {@link #parseAndGetConfigHeaderFiles(PsiElement)} that it's cached
	 * {@link HeaderFile} is no longer valid and it should reparse.
	 */
	public void reparseConfigs(@NotNull PsiFile fileFromModule) {
		synchronized (this) {
			Module module = ModuleUtil.findModuleForPsiElement(fileFromModule);
			if (module == null) {
				return;
			}
			ArmaPluginModuleData moduleData = moduleMap.computeIfAbsent(module, module1 -> {
				return new ArmaPluginModuleData(module);
			});
			moduleData.setReparseConfigHeaderFiles(true);
		}
	}

	/**
	 * This method is just a shortcut that utilizes {@link ArmaPluginApplicationSettings#getState()}
	 *
	 * @return the Arma Tools directory
	 * @see #setArmaToolsDirectory(File)
	 */
	@Nullable
	public File getArmaToolsDirectory() {
		String path = ArmaPluginApplicationSettings.getInstance().getState().armaToolsDirectory;
		if (path == null) {
			return null;
		}
		return new File(path);
	}

	/**
	 * Sets the current Arma Tools directory.
	 * This method is just a shortcut that utilizes {@link ArmaPluginApplicationSettings#getState()}
	 *
	 * @see #getArmaToolsDirectory()
	 */
	public void setArmaToolsDirectory(@Nullable File armaToolsDir) {
		String path = armaToolsDir == null ? null : armaToolsDir.getAbsolutePath();
		ArmaPluginApplicationSettings.getInstance().getState().armaToolsDirectory = path;
	}

	@Nullable
	public XmlFile getStringTableXml(@NotNull PsiElement elementFromModule) {
		synchronized (this) {
			Module module = ModuleUtil.findModuleForPsiElement(elementFromModule);
			if (module == null) {
				return null;
			}
			ArmaPluginModuleData data = moduleMap.computeIfAbsent(module, module1 -> {
				return new ArmaPluginModuleData(module);
			});
			XmlFile f = data.getStringTableXmlFile();
			if (f == null) {
				VirtualFile virtFile = ArmaPluginUtil.getStringTableXmlFile(module);
				if (virtFile == null) {
					return null;
				}
				XmlFile file = (XmlFile) PsiManager.getInstance(elementFromModule.getProject()).findFile(virtFile);
				data.setStringTableXmlFile(file);
			} else {
				if (!f.getVirtualFile().exists()) {
					data.setStringTableXmlFile(null);
					return null;
				}
				return f;
			}
		}
		return null;
	}

	private static class HeaderFileParseErrorNotification extends Notification {
		private final ResourceBundle bundle = ArmaPlugin.getPluginBundle();
		@NotNull
		private final Exception e;

		public HeaderFileParseErrorNotification(@NotNull Exception e) {
			super("Arma Plugin - Header Parse Error", "placeholder", "placeholder", NotificationType.ERROR);
			//using "placeholder" because if we don't, an exception gets thrown for having empty Strings for some reason
			//December 9, 2017
			this.e = e;
		}

		@NotNull
		@Override
		public String getTitle() {
			return bundle.getString("HeaderFileParseErrorNotification.title");
		}

		@NotNull
		@Override
		public String getContent() {
			return String.format(bundle.getString("HeaderFileParseErrorNotification.body_f"), e.getMessage());
		}
	}
}
