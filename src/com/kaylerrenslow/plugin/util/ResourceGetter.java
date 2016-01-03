package com.kaylerrenslow.plugin.util;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Kayler on 01/02/2016.
 */
public class ResourceGetter{

	public static final ResourceGetter instance = new ResourceGetter();

	public URL getResource(String s){
		return getClass().getResource(s);
	}

	public InputStream getResourceAsStream(String s){
		return getClass().getResourceAsStream(s);
	}

}
