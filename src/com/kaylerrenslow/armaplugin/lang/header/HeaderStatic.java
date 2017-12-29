package com.kaylerrenslow.armaplugin.lang.header;

import com.kaylerrenslow.armaplugin.ArmaPlugin;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFStatic;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * @author Kayler
 * @since 12/28/2015
 */
public class HeaderStatic {

	/**
	 * Get the ResourceBundle for the Header language
	 *
	 * @see SQFStatic#getSQFBundle()
	 * @see ArmaPlugin#getPluginBundle()
	 */
	@NotNull
	public static ResourceBundle getHeaderBundle() {
		return ResourceBundle.getBundle("com.kaylerrenslow.armaplugin.HeaderBundle");
	}
}
