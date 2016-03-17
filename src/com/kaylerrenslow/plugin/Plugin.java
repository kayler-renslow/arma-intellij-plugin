package com.kaylerrenslow.plugin;

import com.kaylerrenslow.plugin.util.FileReader;
import com.kaylerrenslow.plugin.util.ResourceBundle;

import java.io.File;

/**
 * Created by Kayler on 12/27/2015.
 */
public class Plugin{

	public static final ResourceBundle resources = new ResourceBundle("/com/kaylerrenslow/plugin/plugin.properties");

	public static final String APP_DATA_FOLDER_NAME = resources.getString("plugin.appdata.folderName");

	public static final File APPDATA_FOLDER = new File(System.getenv().get("APPDATA") + "/" + APP_DATA_FOLDER_NAME);

	public static final PluginUserProperties pluginProps = new PluginUserProperties(APPDATA_FOLDER);

	public static final String SQF_COLOR_SETTINGS_PAGE_TEXT = FileReader.getInstance().getText("/com/kaylerrenslow/plugin/lang/sqf/highlighting/sqfHighlightingDemo.sqf");
	public static final String HEADER_COLOR_SETTINGS_PAGE_TEXT = FileReader.getInstance().getText("/com/kaylerrenslow/plugin/lang/header/highlighting/headerHighlightingDemo.h");


	private static final String t = "true";
	private static final String f = "false";

	public enum UserPropertiesKey{
		VERSION("version", "Version of the instance of when this file was created. (Please don't change this.)", resources.getString("plugin.version"), null),
		SQF_SYNTAX_CHECK("SQF_syntax_check", "Set SQF_syntax_check to true to use the parser that checks syntax for SQF files.\nSet SQF_syntax_check to false to disable the syntax checking.", t, f),
		HEADER_SYNTAX_CHECK("Header_syntax_check", "Set Header_syntax_check to true to use the parser that checks syntax for .h, .hpp, .hh, .ext, and .sqm files.\nSet to false to disable syntax checking.", t, f);

		public final String[] possibleVals;
		public final String keyName;
		public final String defaultValue;
		public final String doc;
		UserPropertiesKey(String keyName, String doc, String defaultVal, String ... possibleVals) {
			this.keyName = keyName;
			this.defaultValue = defaultVal;
			this.doc = doc;
			this.possibleVals = possibleVals;
		}
	}
}
