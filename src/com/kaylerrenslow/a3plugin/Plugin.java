package com.kaylerrenslow.a3plugin;

import com.kaylerrenslow.a3plugin.util.ResourceBundle;

import java.io.File;

/**
 * @author Kayler
 * Created on 12/27/2015.
 */
public class Plugin{

	public static final ResourceBundle resources = new ResourceBundle("/com/kaylerrenslow/a3plugin/plugin.properties");

	private static final String APP_DATA_FOLDER_NAME = resources.getString("plugin.appdata.folderName");

	private static final File APPDATA_FOLDER = new File(System.getenv().get("APPDATA") + "/" + APP_DATA_FOLDER_NAME);

	public static final PluginUserProperties pluginProps = new PluginUserProperties(APPDATA_FOLDER);

	private static final String t = "true";
	private static final String f = "false";

	public static final String VERSION = resources.getString("plugin.version");

	public enum UserPropertiesKey{
		SHOW_MM_ARMA_PLUGIN("Show \"Arma Plugin\" in main menu all the time (true)\nor only when inside Arma Module (false)", t, f),
		SHOW_SCRIPT_ERRORS("Show script errors at the top of the file for SQF and Header files (true)\nor still have error checking but not mark it at the top of the file (false).", t, f);

		public final String[] possibleVals;
		public final String keyName;
		public final String defaultValue;
		public final String doc;
		UserPropertiesKey(String doc, String defaultVal, String ... possibleVals) {
			this.keyName = this.name();
			this.defaultValue = defaultVal;
			this.doc = doc;
			this.possibleVals = possibleVals;
		}
	}
}
