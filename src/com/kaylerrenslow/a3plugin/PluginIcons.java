package com.kaylerrenslow.a3plugin;

import com.intellij.openapi.util.IconLoader;
import com.kaylerrenslow.a3plugin.util.ResourceGetter;

import javax.swing.*;

/**
 * Created by Kayler on 01/01/2016.
 */
public class PluginIcons{
	//http://www.jetbrains.org/intellij/sdk/docs/reference_guide/work_with_icons_and_images.html


	public static final Icon ICON_ARMA3_FILE = IconLoader.getIcon("/com/kaylerrenslow/a3plugin/icons/plugin.png");

	public static final Icon ICON_SQF = IconLoader.getIcon("/com/kaylerrenslow/a3plugin/icons/sqf.png");
	public static final Icon ICON_SQF_COMMAND = IconLoader.getIcon("/com/kaylerrenslow/a3plugin/icons/command.png");
	public static final Icon ICON_SQF_VARIABLE = IconLoader.getIcon("/com/kaylerrenslow/a3plugin/icons/variable.png");

	public static final Icon ICON_HEADER = IconLoader.getIcon("/com/kaylerrenslow/a3plugin/icons/header.png");
	public static final ImageIcon ARMA_LOGO = new ImageIcon(ResourceGetter.getResourceAsURL("/com/kaylerrenslow/a3plugin/img/arma-logo.png"));
}
