package com.kaylerrenslow.a3plugin.lang.header.psi;

import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderClassDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Kayler
 *         This class is a wrapper class for a function that was defined inside the missionConfigFile (description.ext >> CfgFunctions)
 *         Created on 03/30/2016.
 */
public class HeaderConfigFunction {
	private final HeaderClassDeclaration classDeclaration;
	private final String tagName;
	private final String filePath;
	private final String functionFileExtension;
	private final boolean appendFn_;
	private final HeaderClassDeclaration classWithTag;

	/**
	 * This class is a wrapper class for a function that was defined inside the missionConfigFile (description.ext >> CfgFunctions)
	 *
	 * @param classDeclaration        the HeaderClassDeclaration PsiElement that links to the definition
	 * @param containingDirectoryPath file path to the function that is defined in the config (defined from file="exampleFileDir")
	 * @param tagName                 the prefix tag for the function. This is defined in the config with tag="something" (or if not defined, it is the first child class of CfgFunctions)
	 * @param functionFileExtension   file extension (.sqf, .fsm)
	 */
	public HeaderConfigFunction(@NotNull HeaderClassDeclaration classDeclaration, @NotNull String containingDirectoryPath, @NotNull String tagName, @NotNull HeaderClassDeclaration classWithTag, @Nullable String functionFileExtension, boolean appendFn_) {
		this.classDeclaration = classDeclaration;
		this.filePath = containingDirectoryPath;
		this.tagName = tagName;
		this.classWithTag = classWithTag;
		if (functionFileExtension == null) {
			this.functionFileExtension = ".sqf";
		} else {
			this.functionFileExtension = functionFileExtension;
		}
		this.appendFn_ = appendFn_;
	}

	@NotNull
	public HeaderClassDeclaration getClassDeclaration() {
		return this.classDeclaration;
	}

	@NotNull
	public String getFunctionClassName(){
		return this.classDeclaration.getClassName();
	}

	@NotNull
	public HeaderClassDeclaration getClassWithTag() {
		return classWithTag;
	}

	@NotNull
	public String getTagName() {
		return this.tagName;
	}

	@NotNull
	public String getCallableName() {
		return tagName + "_fnc_" + getFunctionClassName();
	}

	@NotNull
	public String getContainingDirectoryPath() {
		return this.filePath;
	}

	@NotNull
	public String getFunctionFileExtension() {
		return this.functionFileExtension;
	}

	/**
	 * Returns true if the file name should be prefixed with 'fn_'. This is false only when the file path is explicity provided inside the function class declaration.<br>
	 *     Example: myFunctionClass {file="hello.sqf";};
	 */
	public boolean appendFn(){
		return this.appendFn_;
	}

	/**
	 * Returns the SQF file name for this function. Can be something like: fn_function.sqf
	 */
	public String getFunctionFileName(){
		return getFunctionFileName(appendFn_, getFunctionClassName(), functionFileExtension);
	}

	public static String getFunctionFileName(boolean appendFn_, String functionClassName, String functionFileExtension){
		return ((appendFn_ ? "fn_" : "") + functionClassName + functionFileExtension);
	}

	/**
	 * Get the full path to this function (\ will be converted to / and fn_ will be appended if required)
	 */
	@NotNull
	public String getFullRelativePath() {
		return getFullRelativePath(getFunctionClassName(), this.filePath, this.functionFileExtension, this.appendFn_);
	}

	@NotNull
	public static String getFullRelativePath(String functionClassName, String functionFilePath, String functionFileExtension, boolean appendFn_){
		return (functionFilePath + (functionFileExtension.length() > 0 ? "\\" : "") + getFunctionFileName(appendFn_, functionClassName, functionFileExtension)).replaceAll("\\\\", "/");
	}

	@NotNull
	public static Icon getIcon() {
		return PluginIcons.ICON_SQF_FUNCTION;
	}
}
