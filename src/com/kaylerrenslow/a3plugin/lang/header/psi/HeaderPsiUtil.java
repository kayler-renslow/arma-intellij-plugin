package com.kaylerrenslow.a3plugin.lang.header.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.a3plugin.lang.header.ConfigClassNotDefinedException;
import com.kaylerrenslow.a3plugin.lang.header.FunctionNotDefinedInConfigException;
import com.kaylerrenslow.a3plugin.lang.header.MalformedConfigException;
import com.kaylerrenslow.a3plugin.lang.header.psi.impl.HeaderConfigFunction;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.project.ArmaProjectDataManager;
import com.kaylerrenslow.a3plugin.util.Attribute;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Kayler
 *         Created on 03/30/2016.
 */
public class HeaderPsiUtil {


	/**
	 * Gets the containing class of the given header element or null if it is undetermined.
	 * <p>It is undetermined if the containing class may be a sub class that is in another file that has headerElement's file included.
	 * For instance, all functions inside CfgFunctions have a containing class (CfgFunctions).
	 * But if CfgFunctions has an include statement inside, each included file has classes inside and those classes are technically subclasses of CfgFunctions.
	 * Finding the containing class from those included classes are therefore undetermined.</p>
	 *
	 * @param headerElement PsiElement that is from a HeaderFile
	 * @return containing class
	 */
	@Nullable
	public static HeaderClassDeclaration getContainingClass(@NotNull PsiElement headerElement) {
		if (!(headerElement.getContainingFile() instanceof HeaderFile)) {
			throw new IllegalArgumentException("header element is not in a header file. it is in file type =" + headerElement.getContainingFile().getClass());
		}
		PsiElement parent = headerElement.getParent();
		while (parent != null) {
			if (parent instanceof HeaderClassDeclaration) {
				return (HeaderClassDeclaration) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	/**
	 * Gets all class declarations where the given attributes are children inside the given class. If className is not null, class declarations will be added if it contains the given attributes and className=classDeclaration.className
	 *
	 * @param start            PsiElement to start at to get class declarations from
	 * @param className        class name to search for, or null if class name doesn't matter
	 * @param attributes       array of attributes to match, or null if attributes don't matter
	 * @param traverseIncludes true if the includes inside the HeaderFile should be traversed, false if to ignore them
	 * @return list of all matched class declarations
	 */
	@NotNull
	public static ArrayList<HeaderClassDeclaration> getClassDeclarationsWithEntriesEqual(@NotNull PsiElement start, @Nullable String className, @Nullable Attribute[] attributes, boolean traverseIncludes) {
		return getClassDeclarationsWithEntriesEqual(start, className, attributes, traverseIncludes, 1, 1);
	}

	/**
	 * Gets all class declarations inside the given class that own the given attributes. If className is not null, class declarations will be added if it contains the given attributes and className=classDeclaration.className
	 *
	 * @param start            PsiElement to start at to get class declarations from
	 * @param className        class name to search for, or null if doesn't matter
	 * @param attributes       array of attributes to match
	 * @param traverseIncludes true if the includes inside the HeaderFile should be traversed, false if to ignore them
	 * @param minDepth         min depth to get classes of (inclusive). Each inner class counts as 1 depth.
	 * @param maxDepth         max depth to get classes of (inclusive). Each inner class counts as 1 depth. If maxDepth == 1, only direct children of start will be returned.
	 * @return list of all matched class declarations
	 * @throws IndexOutOfBoundsException when maxDepth <= 0 or if minDepth <= 0
	 */
	@NotNull
	public static ArrayList<HeaderClassDeclaration> getClassDeclarationsWithEntriesEqual(@NotNull PsiElement start, @Nullable String className, @Nullable Attribute[] attributes, boolean traverseIncludes, int minDepth, int maxDepth) {
		if (maxDepth <= 0) {
			throw new IndexOutOfBoundsException("maxDepth must be >= 1");
		}
		if (minDepth < 1) {
			throw new IndexOutOfBoundsException("minDepth must be >= 1");
		}
		ArrayList<HeaderClassDeclaration> matchedClasses = new ArrayList<>();
		getClassDeclarationsWithEntriesEqual(start, className, attributes, traverseIncludes, matchedClasses, minDepth, maxDepth, 1);
		return matchedClasses;
	}

	private static void getClassDeclarationsWithEntriesEqual(@NotNull PsiElement start, @Nullable String className, @Nullable Attribute[] attributes, boolean traverseIncludes, @NotNull ArrayList<HeaderClassDeclaration> matchedClasses, int minDepth, int maxDepth, int currentDepth) {
		HeaderClassDeclaration classDecl;
		HeaderPreInclude include;
		PsiElement child = start.getFirstChild();
		while (child != null) {
			if (child instanceof HeaderClassDeclaration) {
				classDecl = (HeaderClassDeclaration) child;
				if (currentDepth >= minDepth) {
					if (className == null || classDecl.getClassName().equals(className)) {
						if (attributes == null || classDecl.hasAttributes(attributes, traverseIncludes)) {
							matchedClasses.add(classDecl);
						}
					}
				}
				if (currentDepth + 1 <= maxDepth) {
					if(classDecl.getClassContent() != null){
						getClassDeclarationsWithEntriesEqual(classDecl.getClassContent(), className, attributes, traverseIncludes, matchedClasses, minDepth, maxDepth, currentDepth + 1);
					}
				}
			} else if (traverseIncludes && child instanceof HeaderPreprocessorGroup) {
				HeaderPreprocessorGroup group = (HeaderPreprocessorGroup) child;
				List<HeaderPreprocessor> preprocessors = group.getPreprocessorList();
				for (HeaderPreprocessor preprocessor : preprocessors) {
					if (preprocessor instanceof HeaderPreInclude) {
						include = (HeaderPreInclude) preprocessor;
						HeaderFile f = include.getHeaderFileFromInclude();
						if (f != null) {
							getClassDeclarationsWithEntriesEqual(f, className, attributes, true, matchedClasses, minDepth, maxDepth, currentDepth);
						}
					}
				}
			}else{
				getClassDeclarationsWithEntriesEqual(child, className, attributes, traverseIncludes, matchedClasses, minDepth, maxDepth, currentDepth);
			}
			child = child.getNextSibling();
		}
	}


	/**
	 * Traverses the PsiElement startElement to find the config class with its name=className
	 *
	 * @param startElement     where to start searching for a class declaration with the class declaration's name=className
	 * @param className        class name to search for
	 * @param traverseIncludes true if it should traverse the include statements inside the config file, false if it should search only the given config file
	 * @param minSearchDepth   min depth to search for class declarations. must be >= 1
	 * @param maxSearchDepth   max depth to search for class declarations. must be > 0
	 * @return HeaderClassDeclaration, or null if one couldn't be found
	 */
	@Nullable
	public static HeaderClassDeclaration getClassDeclaration(@NotNull PsiElement startElement, @NotNull String className, boolean traverseIncludes, int minSearchDepth, int maxSearchDepth) {
		ArrayList<HeaderClassDeclaration> decls = getClassDeclarationsWithEntriesEqual(startElement, className, null, traverseIncludes, minSearchDepth, maxSearchDepth);

		if (decls.size() == 0) {
			return null;
		}
		return decls.get(0);
	}


	/**
	 * Gets all included HeaderFiles included inside the given HeaderFile
	 *
	 * @param root root HeaderFile
	 * @return list of all included files
	 */
	@NotNull
	public static ArrayList<HeaderFile> getAllIncludedHeaderFilesInFile(@NotNull HeaderFile root) {
		ArrayList<HeaderFile> includedFiles = new ArrayList<>();
		getAllIncludedHeaderFilesInFile(root, includedFiles);
		return includedFiles;
	}

	private static void getAllIncludedHeaderFilesInFile(@NotNull HeaderFile root, @NotNull ArrayList<HeaderFile> includedFiles) {
		ArrayList<ASTNode> includeStatements = PsiUtil.findDescendantElements(root, HeaderTypes.PRE_INCLUDE, null);
		HeaderPreInclude include;
		for (ASTNode node : includeStatements) {
			include = (HeaderPreInclude) node.getPsi();
			HeaderFile f = include.getHeaderFileFromInclude();
			if (f != null) {
				includedFiles.add(f);
			}
		}
		for (HeaderFile headerFile : includedFiles) {
			getAllIncludedHeaderFilesInFile(headerFile, includedFiles);
		}
	}

	/**
	 * Creates a new HeaderConfigFunction instance for the given functionName
	 *
	 * @param psiFile      HeaderFile instance to retrieve the description.ext for
	 * @param functionName full function name (example: tag_fnc_functionClassName )
	 * @return new HeaderConfigFunction instance for the given function name, or null if the functionName is formatted wrong
	 * @throws ConfigClassNotDefinedException      when CfgFunctions is not defined in description.ext
	 * @throws FileNotFoundException               when there is no description.ext
	 * @throws FunctionNotDefinedInConfigException when the function isn't defined anywhere in CfgFunctions in description.ext
	 * @throws MalformedConfigException            when the config file is incorrectly being used, formatted, or syntactically incorrect
	 */
	@Nullable
	public static HeaderConfigFunction getFunctionFromCfgFunctions(@NotNull PsiFile psiFile, @NotNull String functionName) throws ConfigClassNotDefinedException, FileNotFoundException, FunctionNotDefinedInConfigException, MalformedConfigException {
		//@formatter:off
		/*
		  //some functions
		  overrideTag_fnc_myFunction;
		  overrideTag_fnc_noDefFileFunction;
		  NewTagClass_fnc_function;

		  class CfgFunctions{
		 	class TagClass{ //class with tag
		 		tag="overrideTag"; //use this as the tag instead of TagClass as the tag (this is the only time tag attribute can be set)
		 		class ContainerClass{
		 			file="some\Path";
		 			class myFunction; //this is the function's class declaration. path to this function is "some\Path\fn_myFunction.sqf"
		 		};
		 		class AnotherContainerClass{
		 			class noDefFileFunction; //path: functions\AnotherContainerClass\fn_noDefFileFunction.sqf
		 			class specialFileFunction { //path functions\AnotherContainerClass\totalRadFunction.sqf
		 				file = "totalRadFunction.sqf"
		 			};
		 			class specialFileFunctionWithExt { //path functions\AnotherContainerClass\fn_specialFileFunctionWithExt.fsm
		 				ext = ".fsm";
		 			};
		 		};
		    };
		 	class NewTagClass{
		 		class ContainerClass {
		 			class function;
		 		};
		    };
		  };
		*/
		//@formatter:on
		if (!SQFPsiUtil.followsSQFFunctionNameRules(functionName)) {
			return null;
		}
		HeaderClassDeclaration cfgFuncs = getCfgFunctions(psiFile);
		int _fnc_Index = functionName.indexOf("_fnc_");

		String tagName = functionName.substring(0, _fnc_Index); //the function's prefix tag. exampleTag_fnc_functionClassName
		String functionClassName = functionName.substring(_fnc_Index + 5); //function's class name.
		HeaderClassDeclaration functionClass = null; //psi element that links to the function's class declaration

		Attribute[] tagsAsAttributes = {new Attribute("tag", tagName)};
		ArrayList<HeaderClassDeclaration> matchedClassesWithTag = (getClassDeclarationsWithEntriesEqual(cfgFuncs, null, tagsAsAttributes, true, 1, 1)); //classes inside CfgFunctions that have tag attribute

		if (matchedClassesWithTag.size() == 0) { //no classes of depth 1 relative to CfgFunctions have tag attribute, so find the class itself with its className=tagName
			HeaderClassDeclaration classWithTag = getClassDeclaration(cfgFuncs, tagName, true, 1, 1); //class with tag attribute
			if (classWithTag == null) {
				throw new FunctionNotDefinedInConfigException(functionName);
			}

			functionClass = getClassDeclaration(classWithTag, functionClassName, true, 2, 2); //get the first function class
		} else {
			for (HeaderClassDeclaration matchedTagClass : matchedClassesWithTag) {
				functionClass = getClassDeclaration(matchedTagClass, functionClassName, true, 2, 2); //check if the class that has the tag actually holds the function's class declaration
				if (functionClass != null) {
					break;
				}
			}
			if (functionClass == null) {
				throw new FunctionNotDefinedInConfigException(functionName);
			}
		}

		return getHeaderConfigFunction(cfgFuncs, tagName, functionClass);
	}

	/** Gets a function's details from an already known header class declaration
	 * @param cfgFuncs CfgFunction class declaration
	 * @param tagName tag name of the function
	 * @param functionClass class declaration for the function
	 * @return the HeaderConfigFunction instance representing this function
	 */
	@NotNull
	private static HeaderConfigFunction getHeaderConfigFunction(@NotNull HeaderClassDeclaration cfgFuncs, @NotNull String tagName, @NotNull HeaderClassDeclaration functionClass) throws MalformedConfigException, FunctionNotDefinedInConfigException {
		String containingDirectoryPath = null; //file path (directories)
		String functionFileExtension = null; //file extension (.sqf, .fsm)
		boolean appendFn_ = true; //append fn_ to file name

		Attribute[] functionClassAttributes = functionClass.getAttributes(true); //inner attributes of the function's class declaration
		for (Attribute attribute : functionClassAttributes) { //read the function's inner attributes
			if (attribute.name.equals("file")) {
				if (attribute.value.contains("\\")) {//file path includes a folder
					containingDirectoryPath = attribute.value.substring(0, attribute.value.lastIndexOf('\\')); //has a folder (file='folder\test.sqf')
				} else { //just the file name is inside
					containingDirectoryPath = ""; //no path defined, just the file name (file='test.sqf')
				}
				appendFn_ = false;
				if (isAllowedFunctionExtension(attribute.value, cfgFuncs, functionClass)) {
					functionFileExtension = attribute.value.substring(attribute.value.lastIndexOf('.'));
				}
				break;
			}
			if (attribute.name.equals("ext")) {
				if (isAllowedFunctionExtension(attribute.value, cfgFuncs, functionClass)) {
					functionFileExtension = attribute.value;
				}
			}
		}

		if (containingDirectoryPath == null) { //file path is determined by parent class
			HeaderClassDeclaration containingClassForFunctionClass = getContainingClass(functionClass);
			if (containingClassForFunctionClass != null) {
				Attribute[] attributes = containingClassForFunctionClass.getAttributes(true);
				boolean filePathDefined = false;
				for (Attribute attribute : attributes) {
					if (attribute.name.equals("file")) {
						containingDirectoryPath = attribute.value;
						filePathDefined = true;
						break;
					}
				}
				if (!filePathDefined) {
					containingDirectoryPath = "functions\\" + containingClassForFunctionClass.getClassName();
				}
			} else {
				throw new FunctionNotDefinedInConfigException(tagName + "_fnc_" + functionClass.getClassName());
			}
		}

		return new HeaderConfigFunction(functionClass, containingDirectoryPath, tagName, functionFileExtension, appendFn_);
	}


	/** Get a list of all functions in the CfgFunctions class
	 * @param file any PsiFile inside the current module
	 * @return list of functions
	 * @throws FileNotFoundException when Description.ext doesn't exist
	 * @throws ConfigClassNotDefinedException when CfgFunctions doesn't exist
	 */
	public static ArrayList<HeaderConfigFunction> getAllConfigFunctionsFromDescriptionExt(PsiFile file) throws FileNotFoundException, ConfigClassNotDefinedException {
		HeaderClassDeclaration cfgFunc = getCfgFunctions(file);

		ArrayList<HeaderClassDeclaration> tagClasses = getClassDeclarationsWithEntriesEqual(cfgFunc, null, null, true, 1, 1);
		ArrayList<HeaderConfigFunction> functions = new ArrayList<>();

		for(HeaderClassDeclaration tagClass : tagClasses){
			String tag = null;
			Attribute[] attributes = tagClass.getAttributes(true);
			for(Attribute attribute : attributes){
				if(attribute.name.equals("tag")){
					tag = attribute.value;
				}
			}
			if(tag == null){
				tag = tagClass.getClassName();
			}
			ArrayList<HeaderClassDeclaration> functionClasses = getClassDeclarationsWithEntriesEqual(tagClass, null, null, true, 2, 2);
			try{
				for(HeaderClassDeclaration functionClass : functionClasses){
					functions.add(getHeaderConfigFunction(cfgFunc, tag, functionClass));
				}
			}catch (Exception e){
				//this should never happen
			}
		}

		return functions;
	}


	private static boolean isAllowedFunctionExtension(String value, HeaderClassDeclaration cfgFuncs, HeaderClassDeclaration functionClass) throws MalformedConfigException {
		if (value.endsWith(".sqf") || value.endsWith(".fsm")) {
			return true;
		}
		throw new MalformedConfigException((HeaderFile) cfgFuncs.getContainingFile(), "file type for " + functionClass.getClassName() + " can only be .sqf or .fsm");
	}


	/**
	 * Get's the class declaration to CfgFunctions inside description.ext
	 *
	 * @param psiFile HeaderFile instance to retrieve the description.ext for. Since the project can have multiple modules (which means multiple description.ext's),
	 *                specifying any file in a module will get the description.ext for the HeaderFile's module
	 * @return class declaration
	 * @throws FileNotFoundException          when description.ext doesn't exist
	 * @throws ConfigClassNotDefinedException where CfgFunctions isn't defined inside description.ext
	 */
	@Nullable
	public static HeaderClassDeclaration getCfgFunctions(PsiFile psiFile) throws FileNotFoundException, ConfigClassNotDefinedException {
		HeaderFile file = ArmaProjectDataManager.getInstance().getDataForModule(PluginUtil.getModuleForPsiFile(psiFile)).getDescriptionExt();
		HeaderClassDeclaration cfgFunc = getClassDeclaration(file, "CfgFunctions", true, 1, 1);
		if(cfgFunc == null){
			throw new ConfigClassNotDefinedException("CfgFunctions");
		}
		return cfgFunc;
	}


}
