package com.kaylerrenslow.a3plugin.lang.header.psi.impl;

import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderClassDeclaration;

import javax.swing.*;

/**
 * Created by Kayler on 03/30/2016.
 */
public class HeaderConfigFunction {
	private final HeaderClassDeclaration classDeclaration;
	private final String tagName;
	private final String className;
	private final String filePath;

	public HeaderConfigFunction(HeaderClassDeclaration classDeclaration, String containingDirectoryPath, String tagName, String className) {
		this.classDeclaration = classDeclaration;
		this.filePath =containingDirectoryPath;
		this.tagName = tagName;
		this.className = className;
	}

	public HeaderClassDeclaration getClassDeclaration(){
		return this.classDeclaration;
	}

	public String getCallableName(){
		return tagName + "_fnc_" + this.className;
	}

	public String getContainingDirectoryPath(){
		return this.filePath;
	}

	public Icon getIcon(){
		return PluginIcons.ICON_SQF_FUNCTION;
	}
}
