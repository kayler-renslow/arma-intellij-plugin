package com.kaylerrenslow.armaplugin;

import com.kaylerrenslow.armaplugin.lang.sqf.SQFStatic;
import org.jetbrains.annotations.NotNull;

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
}
