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
		File f = new File(appdataFolder.getPath() + "/"+ Static.PLUGIN_PROPERTIES_FILE);
		if(!f.exists()){
			createPluginProps(f);
		}
		try{
			FileInputStream fis = new FileInputStream(f);
			pluginProps.load(fis);
		}catch(Exception e){
			//e.printStackTrace();
		}
	}

	private void createPluginProps(File f) {
		try{
			f.createNewFile();
			PrintWriter pw = new PrintWriter(f);
			pw.println("#All changes made to this file will take effect when Intellij is restarted.\n");
			for(Static.PluginPropertiesKey ppk : Static.PluginPropertiesKey.values()){
				pw.println("#" + ppk.doc);
				pw.println(ppk.keyName + "=" + ppk.defaultValue);
				pw.println();
			}
			pw.flush();
			pw.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
}
