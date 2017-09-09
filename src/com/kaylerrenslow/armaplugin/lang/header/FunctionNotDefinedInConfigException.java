package com.kaylerrenslow.armaplugin.lang.header;

/**
 * @author Kayler
 * @since 03/30/2016
 */
public class FunctionNotDefinedInConfigException extends GenericConfigException {
	private static final String format = "Function '%s' is not defined in CfgFunctions config";

	public FunctionNotDefinedInConfigException(String className) {
		super(String.format(format, className));
	}
}
