package com.kaylerrenslow.a3plugin.lang.header.exception;

/**
 * Base exception class for any type of exception that occurs while reading a config
 *
 * @author Kayler
 * @since 04/08/2016
 */
public class GenericConfigException extends Exception {
	public GenericConfigException(String message) {
		super(message);
	}
}
