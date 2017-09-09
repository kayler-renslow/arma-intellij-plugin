package com.kaylerrenslow.armaplugin.lang.header;

/**
 * Exception for when the description.ext or config.cpp file doesn't exist
 *
 * @author Kayler
 * @since 09/09/2017
 */
public class RootConfigNotDefinedException extends GenericConfigException {

	public RootConfigNotDefinedException() {
		super(HeaderStatic.getHeaderBundle().getString("missing-root-config"));
	}
}
