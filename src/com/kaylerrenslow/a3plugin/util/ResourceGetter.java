package com.kaylerrenslow.a3plugin.util;

import java.io.InputStream;
import java.net.URL;

/**
 * @author Kayler
 * Gets resources from inside the build path.
 * Created on 01/02/2016.
 */
public class ResourceGetter{

	private ResourceGetter(){}

	private static final ResourceGetter instance = new ResourceGetter();

	/**Get the resource at location s from the build path as a URL*/
	public static URL getResourceAsURL(String s){
		return instance.getClass().getResource(s);
	}

	/**Get the resource at location s from the build path as an InputStream*/
	public static InputStream getResourceAsStream(String s){
		return instance.getClass().getResourceAsStream(s);
	}

}
