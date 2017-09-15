package com.kaylerrenslow.armaplugin;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * @author Kayler
 * @since 01/01/2016
 */
public class ArmaPluginIcons {

	public static final Icon ICON_ARMA3_FILE = IconLoader.getIcon("icons/plugin.png");

	public static final Icon ICON_SQF = IconLoader.getIcon(path("icons/sqf.png"));
	public static final Icon ICON_SQF_COMMAND = IconLoader.getIcon(path("icons/command.png"));
	public static final Icon ICON_SQF_VARIABLE = IconLoader.getIcon(path("icons/variable.png"));
	public static final Icon ICON_SQF_MAGIC_VARIABLE = IconLoader.getIcon(path("icons/magic_variable.png"));
	public static final Icon ICON_SQF_FUNCTION = IconLoader.getIcon(path("icons/function.png"));

	public static final Icon ICON_HEADER = IconLoader.getIcon(path("icons/header.png"));

	private static String path(String p) {
		return "/com/kaylerrenslow/armaplugin/" + p;
	}
}
