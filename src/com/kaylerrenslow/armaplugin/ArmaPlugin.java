package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFStatic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ResourceBundle;

/**
 * Utility methods and static fields for the Arma Intellij Plugin
 *
 * @author Kayler
 * @since 05/18/2017
 */
public class ArmaPlugin {
	/**
	 * Get the ResourceBundle for the plugin
	 *
	 * @see SQFStatic#getSQFBundle()
	 * @see com.kaylerrenslow.armaplugin.lang.header.HeaderStatic#getHeaderBundle()
	 */
	@NotNull
	public static ResourceBundle getPluginBundle() {
		return ResourceBundle.getBundle("com.kaylerrenslow.armaplugin.PluginBundle");
	}

	/**
	 * @return the path to Arma IntelliJ Plugin's temp directory for the given module,
	 * or null if the .iml directory couldn't be located
	 */
	@Nullable
	public static String getPathToTempDirectory(@NotNull Module module) {
		final String tempFolder = "/armaplugin-temp";

		//find a place to save parse data
		VirtualFile imlVirtFile = module.getModuleFile();
		if (imlVirtFile == null) {
			String projectBasePath = module.getProject().getBasePath();
			if (projectBasePath == null) {
				return null;
			}
			return projectBasePath + tempFolder;
		}
		VirtualFile imlDir = imlVirtFile.getParent();
		if (imlDir == null) {
			return null;
		}
		return imlDir.getPath() + tempFolder;
	}
}
