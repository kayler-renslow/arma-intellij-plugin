package com.kaylerrenslow.a3plugin.dialog;

import com.intellij.openapi.module.Module;
import com.kaylerrenslow.a3plugin.util.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 *         Class that holds information for a config function (function defined inside description.ext/CfgFunctions
 *         Created on 04/25/2016.
 */
public class SQFConfigFunctionInformationHolder {
	/**
	 * Function tag name
	 */
	public final String functionTagName;

	/**
	 * Function config class name (class name inside description.ext/CfgFunctions)
	 */
	public final String functionClassName;

	/**
	 * String containing path to the function. This will be something like 'folder\anotherFolder'
	 */
	public final String functionLocation;

	/**
	 * Module that the function resides in
	 */
	public final Module module;

	/**
	 * Special attributes about the function
	 */
	public final Attribute[] attributes;

	public SQFConfigFunctionInformationHolder(@NotNull String functionTagName, @NotNull String functionClassName, @NotNull String functionLocation, @NotNull Module module, @Nullable Attribute[] attributes) {
		this.functionTagName = functionTagName;
		this.functionClassName = functionClassName;
		this.functionLocation = functionLocation;
		this.module = module;
		this.attributes = attributes;
	}
}
