package com.kaylerrenslow.plugin;

import com.intellij.openapi.util.IconLoader;
import com.kaylerrenslow.plugin.lang.header.HeaderStatic;
import com.kaylerrenslow.plugin.lang.sqf.SQFStatic;

import javax.swing.*;

/**
 * Created by Kayler on 10/31/2015.
 */
public class Static {
    public static final String version = "1.0.0";

    public static final Icon ICON_FILE = IconLoader.getIcon("/com/kaylerrenslow/plugin/icons/icon.png"); //http://www.jetbrains.org/intellij/sdk/docs/reference_guide/work_with_icons_and_images.html

    public static final String PLUGIN_PROPERTIES_FILE = "plugin.properties";
    public static final String APP_DATA_FOLDER = "Arma 3 Intellij Plugin";

    public enum PluginPropertiesKey{
        VERSION("version", Static.version, "Version of the plugin of when this file was created. (Please don't change this.)"),
        SQF_SYNTAX_CHECK("SQF_syntax_check", "true", "Set SQF_syntax_check to true to use the parser that checks syntax. Set SQF_syntax_check to false to disable the syntax checking.");

        public String keyName;
        public String defaultValue;
        public String doc;
        PluginPropertiesKey(String keyName, String defaultVal, String doc) {
            this.keyName = keyName;
            this.defaultValue = defaultVal;
            this.doc = doc;
        }
    }

    public static final Plugin plugin = new Plugin();

}
