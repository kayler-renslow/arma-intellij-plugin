package com.kaylerrenslow.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * Created by Kayler on 12/27/2015.
 */
public class Plugin{
	private Properties pluginProps = new Properties();
	public final File appdataFolder = new File(System.getenv().get("APPDATA") + "/" + Static.APP_DATA_FOLDER);
	private File pluginPropsFile;
	public Plugin() {
		load();
	}

	public String getPluginProperty(Static.PluginPropertiesKey key){
		return pluginProps.getProperty(key.keyName, key.defaultValue);
	}

	private void load(){
		loadPluginProps();
	}

	private void loadPluginProps() {
		if(!appdataFolder.exists()){
			appdataFolder.mkdir();
		}
		pluginPropsFile = new File(appdataFolder.getPath() + "/"+ Static.PLUGIN_PROPERTIES_FILE);
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
		for(Static.PluginPropertiesKey ppk : Static.PluginPropertiesKey.values()){
			pluginProps.put(ppk.keyName, ppk.defaultValue);
		}
		savePluginPropsToFile();
	}

	public void overridePluginProps(Static.PluginPropertiesKey key, String newValue){
		pluginProps.setProperty(key.keyName, newValue);
	}

	public void savePluginPropsToFile(){
		try{
			if(!pluginPropsFile.exists()){
				pluginPropsFile.createNewFile();
			}
			PrintWriter pw = new PrintWriter(pluginPropsFile);
			pw.println("#All changes made to this file will take effect when Intellij is restarted.\n");
			for(Static.PluginPropertiesKey ppk : Static.PluginPropertiesKey.values()){
				pw.println("#" + ppk.doc);
				pw.println(ppk.keyName + "=" + pluginProps.getProperty(ppk.keyName));
				pw.println();
			}
			pw.flush();
			pw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
