package com.kaylerrenslow.a3plugin.lang.header;

/**
 * Created by Kayler on 03/30/2016.
 */
public class FunctionNotDefinedInConfigException extends Exception {
	private static final String format = "Function '%s' is not defined in CfgFunctions config";

	public FunctionNotDefinedInConfigException(String className) {
		super(String.format(format, className));
	}
}
