package com.kaylerrenslow.a3plugin.lang.header.psi;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.header.exception.DescriptionExtNotDefinedException;
import com.kaylerrenslow.a3plugin.project.ArmaProjectDataManager;
import com.kaylerrenslow.a3plugin.util.FilePath;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * This class is a wrapper class for a function that was defined inside the missionConfigFile (description.ext >> CfgFunctions)
 *
 * @author Kayler
 * @since 03/30/2016
 */
public class HeaderConfigFunction {
	private final HeaderClassDeclaration classDeclaration;
	private final String tagName, filePath, fileNameNoExt, functionFileExtension;
	private final boolean appendFn_;
	private final HeaderClassDeclaration classWithTag;

	/**
	 * This class is a wrapper class for a function that was defined inside the missionConfigFile (description.ext >> CfgFunctions)
	 *
	 * @param classDeclaration        the HeaderClassDeclaration PsiElement that links to the definition
	 * @param containingDirectoryPath file path to the function that is defined in the config (defined from file="exampleFileDir")
	 * @param fileNameNoExt           file name of the function, or null if determined by the function class name
	 * @param tagName                 the prefix tag for the function. This is defined in the config with tag="something" (or if not defined, it is the first child class of CfgFunctions)
	 * @param functionFileExtension   file extension (.sqf, .fsm)
	 */
	public HeaderConfigFunction(@NotNull HeaderClassDeclaration classDeclaration, @NotNull String containingDirectoryPath, @Nullable String fileNameNoExt, @NotNull String tagName, @NotNull HeaderClassDeclaration classWithTag, @Nullable String functionFileExtension) {
		this.classDeclaration = classDeclaration;
		this.filePath = containingDirectoryPath;
		this.tagName = tagName;
		this.classWithTag = classWithTag;
		if (functionFileExtension == null) {
			this.functionFileExtension = ".sqf";
		} else {
			this.functionFileExtension = functionFileExtension;
		}
		this.appendFn_ = fileNameNoExt == null;
		if (fileNameNoExt == null) {
			this.fileNameNoExt = getFunctionFileName(this.getFunctionClassName(), ""); //don't append file extension
		} else {
			this.fileNameNoExt = fileNameNoExt;
		}

	}

	@NotNull
	public HeaderClassDeclaration getClassDeclaration() {
		return this.classDeclaration;
	}

	@NotNull
	public String getFunctionClassName() {
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

	/**
	 * Get the file path defined in the config (e.g. folder1/folder2)
	 */
	@NotNull
	public String getContainingDirectoryPath() {
		return this.filePath;
	}

	/**
	 * Get file extension (.sqf, .fsm)
	 */
	@NotNull
	public String getFunctionFileExtension() {
		return this.functionFileExtension;
	}

	/**
	 * Returns true if the file name should be prefixed with 'fn_'. This is false only when the file path is explicity provided inside the function class declaration.<br>
	 * Example: myFunctionClass {file="hello.sqf";};
	 */
	public boolean appendFn() {
		return this.appendFn_;
	}

	/**
	 * Returns the file name for this function. Can be something like: fn_function.sqf or hello.sqf
	 */
	public String getFunctionFileName() {
		return this.fileNameNoExt + this.functionFileExtension;
	}

	/**
	 * Get a function file name that is generated by the function class name. Example output= fn_functionClassName.sqf
	 *
	 * @param functionClassName     function class name
	 * @param functionFileExtension extension (.sqf, .ext)
	 * @return file name
	 */
	public static String getFunctionFileName(String functionClassName, String functionFileExtension) {
		return "fn_" + functionClassName + functionFileExtension;
	}

	/**
	 * Get the full path to this function (\ will be converted to / and fn_ will be appended if required)
	 */
	@NotNull
	public String getFullRelativePath() {
		return (this.filePath + "/" + this.fileNameNoExt + this.functionFileExtension).replaceAll("\\\\", "/");
	}


	@NotNull
	public static Icon getIcon() {
		return PluginIcons.ICON_SQF_FUNCTION;
	}

	/**
	 * Get the psi file associated with this function
	 */
	@Nullable
	public PsiFile getPsiFile() {
		PsiDirectory rootMissionDirectory;
		try {
			Module module = PluginUtil.getModuleForPsiFile(getClassDeclaration().getContainingFile());
			rootMissionDirectory = ArmaProjectDataManager.getInstance().getDataForModule(module).getRootMissionDirectory();
		} catch (DescriptionExtNotDefinedException e) {
			e.printStackTrace(System.out);
			return null;
		}
		return PluginUtil.findFileByPath(FilePath.getFilePathFromString(getFullRelativePath(), FilePath.DEFAULT_DELIMETER), rootMissionDirectory);

	}
}
