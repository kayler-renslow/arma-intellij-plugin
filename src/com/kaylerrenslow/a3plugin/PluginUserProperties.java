package com.kaylerrenslow.a3plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * @author Kayler
 * Created on 12/30/2015.
 */
public class PluginUserProperties{
	private static final String PROPERTIES_FILE = "plugin.properties";

	private Properties pluginProps = new Properties();

	private File pluginPropsFile;
	private File appdataFolder;

	public PluginUserProperties(File appdataFolder) {
		this.appdataFolder = appdataFolder;
		load();
	}

	public String getPluginProperty(Plugin.UserPropertiesKey key){
		return pluginProps.getProperty(key.keyName, key.defaultValue);
	}

	public boolean propertyIsTrue(Plugin.UserPropertiesKey key){
		return getPluginProperty(key).equalsIgnoreCase("true");
	}

	private void load(){
		if(!appdataFolder.exists()){
			appdataFolder.mkdir();
		}
		pluginPropsFile = new File(appdataFolder.getPath() + "/"+ PROPERTIES_FILE);
		if(!pluginPropsFile.exists()){
			createPluginProps();
		}
		try{
			FileInputStream fis = new FileInputStream(pluginPropsFile);
			pluginProps.load(fis);
		}catch(Exception e){
			//e.printStackTrace();
		}
	}

	private void createPluginProps() {
		for(Plugin.UserPropertiesKey ppk : Plugin.UserPropertiesKey.values()){
			pluginProps.put(ppk.keyName, ppk.defaultValue);
		}
		savePluginPropsToFile();
	}

	public void overridePluginProps(Plugin.UserPropertiesKey key, String newValue){
		pluginProps.setProperty(key.keyName, newValue);
	}


	/**Saves the plugin properties to file in appdata. Returns true if the save was successful, false otherwise*/
	public boolean savePluginPropsToFile(){
		try{
			if(!pluginPropsFile.exists()){
				pluginPropsFile.createNewFile();
			}
			PrintWriter pw = new PrintWriter(pluginPropsFile);
			pw.println("#All changes made to this file will take effect when Intellij is restarted.\n");
			for(Plugin.UserPropertiesKey ppk : Plugin.UserPropertiesKey.values()){
				pw.println("#" + ppk.doc.replaceAll("\n", " "));
				pw.println(ppk.keyName + "=" + pluginProps.getProperty(ppk.keyName));
				pw.println();
			}
			pw.flush();
			pw.close();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}


}
