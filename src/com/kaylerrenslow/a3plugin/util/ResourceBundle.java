package com.kaylerrenslow.a3plugin.util;

import java.util.Properties;

/**
 * @since 01/01/2016
 */
public class ResourceBundle {
	private Properties props = new Properties();

	public ResourceBundle(String packagePath) {
		try {
			props.load(getClass().getResourceAsStream(packagePath));
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public String getString(String key) {
		return props.getProperty(key);
	}

}
