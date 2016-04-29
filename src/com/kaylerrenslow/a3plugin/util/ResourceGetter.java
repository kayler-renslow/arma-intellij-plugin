package com.kaylerrenslow.a3plugin.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Kayler
 * Gets resources from inside the build path.
 * Created on 01/02/2016.
 */
public class ResourceGetter{

	private ResourceGetter(){}


	/**Get the resource at location s from the build path as a URL. <b>Do not use this for getting things inside the JAR</b>*/
	@Nullable
	public static URL getResourceAsURL(@NotNull String s){
		return ResourceGetter.class.getResource(s);
	}

	/**Get the resource at location s from the build path as an InputStream*/
	public static InputStream getResourceAsStream(@NotNull String s){
		return ResourceGetter.class.getResourceAsStream(s);
	}

}
