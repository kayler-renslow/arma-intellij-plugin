package com.kaylerrenslow.a3plugin.util;

import java.io.File;
import java.net.URL;

/**
 * Created by Kayler on 01/02/2016.
 */
public class PluginUtil{

	public static File convertURLToFile(URL url){
		File f;
		try{
			f = new File(url.toURI());
			f = new File(f.getPath());
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return f;
	}

}
