package com.kaylerrenslow.armaplugin.lang.header;

import javax.annotation.Nullable;

/**
 * Base exception class for any type of exception that occurs while reading a config
 *
 * @author Kayler
 * @since 04/08/2016
 */
public class GenericConfigException extends Exception {
	public GenericConfigException(@Nullable String message) {
		super(message);
	}
}
