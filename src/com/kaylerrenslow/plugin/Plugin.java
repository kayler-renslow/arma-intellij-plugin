package com.kaylerrenslow.plugin;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.io.File;

/**
 * Created by Kayler on 12/27/2015.
 */
public class Plugin{

	public static final String APP_DATA_FOLDER_NAME = "Arma 3 Intellij Plugin";

	public static final File APPDATA_FOLDER = new File(System.getenv().get("APPDATA") + "/" + APP_DATA_FOLDER_NAME);
	public static final String VERSION = "1.0.0";

	public static final Icon ICON_FILE = IconLoader.getIcon("/com/kaylerrenslow/plugin/icons/icon.png"); //http://www.jetbrains.org/intellij/sdk/docs/reference_guide/work_with_icons_and_images.html

	public static final PluginProperties pluginProps = new PluginProperties(APPDATA_FOLDER);

	public static final PluginNonStaticLoader LOADER = new PluginNonStaticLoader();

	private static final String t = "true";
	private static final String f = "false";

	public enum PluginPropertiesKey{
		VERSION("version", "Version of the instance of when this file was created. (Please don't change this.)", Plugin.VERSION, null),
		SQF_SYNTAX_CHECK("SQF_syntax_check", "Set SQF_syntax_check to true to use the parser that checks syntax for SQF files.\nSet SQF_syntax_check to false to disable the syntax checking.", t, f),
		HEADER_SYNTAX_CHECK("Header_syntax_check", "Set Header_syntax_check to true to use the parser that checks syntax for .h, .hpp, .hh, .ext, and .sqm files.\nSet to false to disable syntax checking.", t, f);

		public final String[] possibleVals;
		public final String keyName;
		public final String defaultValue;
		public final String doc;
		PluginPropertiesKey(String keyName, String doc, String defaultVal, String ... possibleVals) {
			this.keyName = keyName;
			this.defaultValue = defaultVal;
			this.doc = doc;
			this.possibleVals = possibleVals;
		}
	}
}
