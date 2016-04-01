package com.kaylerrenslow.a3plugin.lang.header.psi.impl;

import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderClassDeclaration;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Kayler
 * This class is a wrapper class for a function that was defined inside the missionConfigFile (description.ext >> CfgFunctions)
 * Created on 03/30/2016.
 */
public class HeaderConfigFunction {
	private final HeaderClassDeclaration classDeclaration;
	private final String tagName;
	private final String className;
	private final String filePath;
	private final String functionFileExtension;

	/** This class is a wrapper class for a function that was defined inside the missionConfigFile (description.ext >> CfgFunctions)
	 * @param classDeclaration the HeaderClassDeclaration PsiElement that links to the definition
	 * @param containingDirectoryPath file path to the function that is defined in the config (defined from file="exampleFileDir")
	 * @param tagName the prefix tag for the function. This is defined in the config with tag="something" (or if not defined, it is the first child class of CfgFunctions)
	 * @param className class declaration name for funciton
	 * @param functionFileExtension file extension (.sqf, .fsm)
	 */
	public HeaderConfigFunction(HeaderClassDeclaration classDeclaration, String containingDirectoryPath, String tagName, String className, @Nullable String functionFileExtension) {
		this.classDeclaration = classDeclaration;
		this.filePath =containingDirectoryPath;
		this.tagName = tagName;
		this.className = className;
		if(functionFileExtension == null){
			this.functionFileExtension = ".sqf";
		}else{
			this.functionFileExtension = functionFileExtension;
		}
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

	public String getFunctionFileExtension(){
		return this.functionFileExtension;
	}

	public String getFullRelativePath(){
		return this.filePath + (this.filePath.length() > 0 ? "\\" : "") + this.className + this.functionFileExtension;
	}

	public static Icon getIcon(){
		return PluginIcons.ICON_SQF_FUNCTION;
	}
}
