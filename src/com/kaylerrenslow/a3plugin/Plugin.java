package com.kaylerrenslow.a3plugin;

import com.kaylerrenslow.a3plugin.util.FileReader;
import com.kaylerrenslow.a3plugin.util.ResourceBundle;

import java.io.File;

/**
 * Created by Kayler on 12/27/2015.
 */
public class Plugin{

	public static final ResourceBundle resources = new ResourceBundle("/com/kaylerrenslow/a3plugin/plugin.properties");

	public static final String APP_DATA_FOLDER_NAME = resources.getString("plugin.appdata.folderName");

	public static final File APPDATA_FOLDER = new File(System.getenv().get("APPDATA") + "/" + APP_DATA_FOLDER_NAME);

	public static final PluginUserProperties pluginProps = new PluginUserProperties(APPDATA_FOLDER);

	private static final String t = "true";
	private static final String f = "false";

	public enum UserPropertiesKey{
		VERSION("version", "Version of the instance of when this file was created. (Please don't change this.)", resources.getString("plugin.version"), null);

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
