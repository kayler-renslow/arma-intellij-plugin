package com.kaylerrenslow.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * Created by Kayler on 12/30/2015.
 */
public class PluginProperties{
	private static final String PROPERTIES_FILE = "plugin.properties";

	private Properties pluginProps = new Properties();

	private File pluginPropsFile;
	private File appdataFolder;

	public PluginProperties(File appdataFolder) {
		this.appdataFolder = appdataFolder;
		load();
	}

	public String getPluginProperty(Plugin.PluginPropertiesKey key){
		return pluginProps.getProperty(key.keyName, key.defaultValue);
	}

	private void load(){
		loadPluginProps();
	}

	private void loadPluginProps() {
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
		for(Plugin.PluginPropertiesKey ppk : Plugin.PluginPropertiesKey.values()){
			pluginProps.put(ppk.keyName, ppk.defaultValue);
		}
		savePluginPropsToFile();
	}

	public void overridePluginProps(Plugin.PluginPropertiesKey key, String newValue){
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
			for(Plugin.PluginPropertiesKey ppk : Plugin.PluginPropertiesKey.values()){
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
