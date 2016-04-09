package com.kaylerrenslow.a3plugin.lang.header.exception;

import com.kaylerrenslow.a3plugin.util.Attribute;

import java.util.Arrays;

/**
 * @author Kayler
 * This exception is created when trying to find a class inside a header config file and it doesn't exist
 * Created on 03/30/2016.
 */
public class ConfigClassNotDefinedException extends GenericConfigException {
	private static final String classNotDefinedStringF = "Config class '%s' doesn't exist";
	private static final String classNotDefinedWithAttrStringF = "There is no config class with name '%s' and attributes: %s";
	private static final String couldNotFindClassWithAttrF = "There is no known config class with attributes: %s";

	public ConfigClassNotDefinedException(String className) {
		super(String.format(classNotDefinedStringF, className));
	}

	public ConfigClassNotDefinedException(Attribute[] attributes) {
		super(String.format(couldNotFindClassWithAttrF, Arrays.toString(attributes)));
	}

	public ConfigClassNotDefinedException(String className, Attribute[] attributes) {
		super(String.format(classNotDefinedWithAttrStringF, className, Arrays.toString(attributes)));
	}


}
