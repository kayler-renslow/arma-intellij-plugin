package com.kaylerrenslow.a3plugin.lang.header.exception;

/**
 * @author Kayler
 * Base exception class for any type of exception that occurs while reading a config
 * Created on 04/08/2016.
 */
public class GenericConfigException extends Exception {
	public GenericConfigException(String message) {
		super(message);
	}
}
