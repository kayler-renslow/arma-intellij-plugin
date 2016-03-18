package com.kaylerrenslow.plugin;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by Kayler on 01/01/2016.
 */
public class PluginIcons{
	//http://www.jetbrains.org/intellij/sdk/docs/reference_guide/work_with_icons_and_images.html

	public static final Icon ICON_ARMA3_FILE = IconLoader.getIcon(Plugin.resources.getString("lang.icon.file"));

	public static final Icon ICON_SQF = IconLoader.getIcon(Plugin.resources.getString("lang.sqf.icon.file"));
	public static final Icon ICON_SQF_COMMAND = IconLoader.getIcon(Plugin.resources.getString("lang.sqf.icon.command"));


	public static final Icon ICON_HEADER = IconLoader.getIcon(Plugin.resources.getString("lang.header.icon.file"));
}
