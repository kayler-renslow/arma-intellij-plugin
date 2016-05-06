package com.kaylerrenslow.a3plugin.lang.header.psi;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.dialog.SQFConfigFunctionInformationHolder;
import com.kaylerrenslow.a3plugin.lang.header.HeaderFileType;
import com.kaylerrenslow.a3plugin.lang.header.exception.*;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.project.ArmaProjectDataManager;
import com.kaylerrenslow.a3plugin.util.Attribute;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @author Kayler
 *         Created on 03/30/2016.
 */
public class HeaderPsiUtil {

	/**
	 * Creates a new config function and inserts it into CfgFunctions. The CfgFunctions is determined by the module inside newFunction.module
	 *
	 * @param newFunction class that contains information about the new function
	 * @throws GenericConfigException when class failed to be inserted
	 */
	public static void insertNewFunctionIntoCfgFunctions(@NotNull SQFConfigFunctionInformationHolder newFunction) throws GenericConfigException {
		Project project = newFunction.module.getProject();

		HeaderClassDeclaration cfgFunctions = getCfgFunctions(newFunction.module);

		HeaderClassDeclaration definedTagClass = null; //class that contains the tag for the function
		HeaderClassDeclaration definedContainerClass = null; //class that contains the class declaration for the function class


		Attribute[] tagAttribute = {new Attribute("tag", "\"" + newFunction.functionTagName + "\"")};
		Attribute[] fileAttribute = {new Attribute("file", "\"" + newFunction.functionLocation + "\"")};

		//get all tag classes inside cfg functions
		ArrayList<HeaderClassDeclaration> tagClasses = getClassDeclarationsWithEntriesEqual(cfgFunctions, null, null, true, 1, 1);

		// check if tag is defined
		for (HeaderClassDeclaration tagClass : tagClasses) {
			if (tagClass.hasAttributes(tagAttribute, true)) {
				definedTagClass = tagClass;
			}
		}
		//tag attribute wasn't found, just check class names now
		if (definedTagClass == null) {
			for (HeaderClassDeclaration tagClass : tagClasses) {
				if (tagClass.getClassName().equals(newFunction.functionTagName)) {
					definedTagClass = tagClass;
				}
			}
		}
		//tag hasn't been defined yet. create a new class
		if (definedTagClass == null) {
			definedTagClass = HeaderPsiUtil.createClassDeclaration(project, "Tag_Class_" + newFunction.functionTagName, tagAttribute);

			//write new tag class to CfgFunctions
			cfgFunctions.getClassContent().getFileEntries().getNode().addChild(createFileEntryForClass(project, definedTagClass).getNode());
		}

		//check if file path has been defined
		ArrayList<HeaderClassDeclaration> containerClasses = getClassDeclarationsWithEntriesEqual(definedTagClass, null, null, true, 1, 1); //all container classes for the given tag class

		// find container class where the path matches and is inside the tag class
		for (HeaderClassDeclaration containerClass : containerClasses) {
			if (containerClass.hasAttributes(fileAttribute, true)) {
				definedContainerClass = containerClass;
			}
		}
		// if there is no class with matching path, create new container class with path=location
		if (definedContainerClass == null) {
			String name = "Container_Class_" + System.currentTimeMillis();
			definedContainerClass = createClassDeclaration(project, name, fileAttribute);
			definedTagClass.getClassContent().getFileEntries().getNode().addChild(createFileEntryForClass(project, definedContainerClass).getNode()); //add the new container class to the defined tag class
		}

		// insert function declaration class in containing class
		HeaderClassDeclaration functionClass = createClassDeclaration(project, newFunction.functionClassName, newFunction.attributes); //new function class declaration

		if (definedContainerClass.getClassContent() == null) {
			definedContainerClass.getNode().addChild(createClassDeclaration(project, "t", null).getClassContent().getNode());
		}
		definedContainerClass.getClassContent().getFileEntries().getNode().addChild(createFileEntryForClass(project, functionClass).getNode());

	}


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
					if (classDecl.getClassContent() != null) {
						getClassDeclarationsWithEntriesEqual(classDecl.getClassContent(), className, attributes, traverseIncludes, matchedClasses, minDepth, maxDepth, currentDepth + 1);
					}
				}
			} else if (traverseIncludes && child instanceof HeaderPreInclude) {
				include = (HeaderPreInclude) child;
				PsiFile f = include.getHeaderFileFromInclude();
				if (f != null && f instanceof HeaderFile) {
					getClassDeclarationsWithEntriesEqual(f, className, attributes, true, matchedClasses, minDepth, maxDepth, currentDepth);
				}

			} else {
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
	 * Creates a new HeaderConfigFunction instance for the given functionName
	 *
	 * @param psiFile      HeaderFile instance to retrieve the description.ext for
	 * @param functionName full function name (example: tag_fnc_functionClassName )
	 * @return new HeaderConfigFunction instance for the given function name, or null if the functionName is formatted wrong
	 * @throws ConfigClassNotDefinedException      when CfgFunctions is not defined in description.ext
	 * @throws DescriptionExtNotDefinedException   when there is no description.ext
	 * @throws FunctionNotDefinedInConfigException when the function isn't defined anywhere in CfgFunctions in description.ext
	 * @throws MalformedConfigException            when the config file is incorrectly being used, formatted, or syntactically incorrect
	 */
	@Nullable
	public static HeaderConfigFunction getFunctionFromCfgFunctions(@NotNull PsiFile psiFile, @NotNull String functionName) throws ConfigClassNotDefinedException, DescriptionExtNotDefinedException, FunctionNotDefinedInConfigException, MalformedConfigException {
		HeaderClassDeclaration cfgFuncs = getCfgFunctions(psiFile);
		return getFunctionFromCfgFunctionsBody(functionName, cfgFuncs);
	}

	/**
	 * Creates a new HeaderConfigFunction instance for the given functionName
	 *
	 * @param module       Module that contains the description.ext
	 * @param functionName full function name (example: tag_fnc_functionClassName )
	 * @return new HeaderConfigFunction instance for the given function name, or null if the functionName is formatted wrong
	 * @throws ConfigClassNotDefinedException      when CfgFunctions is not defined in description.ext
	 * @throws DescriptionExtNotDefinedException   when there is no description.ext
	 * @throws FunctionNotDefinedInConfigException when the function isn't defined anywhere in CfgFunctions in description.ext
	 * @throws MalformedConfigException            when the config file is incorrectly being used, formatted, or syntactically incorrect
	 */
	@Nullable
	public static HeaderConfigFunction getFunctionFromCfgFunctions(@NotNull Module module, @NotNull String functionName) throws ConfigClassNotDefinedException, DescriptionExtNotDefinedException, FunctionNotDefinedInConfigException,
			MalformedConfigException {
		HeaderClassDeclaration cfgFuncs = getCfgFunctions(module);
		return getFunctionFromCfgFunctionsBody(functionName, cfgFuncs);
	}

	@Nullable
	private static HeaderConfigFunction getFunctionFromCfgFunctionsBody(@NotNull String functionName, HeaderClassDeclaration cfgFuncs) throws FunctionNotDefinedInConfigException, MalformedConfigException {
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
		if (!SQFStatic.followsSQFFunctionNameRules(functionName)) {
			return null;
		}
		SQFStatic.SQFFunctionTagAndName tagAndClass = SQFStatic.getFunctionTagAndName(functionName);
		String tagName = tagAndClass.tagName;
		String functionClassName = tagAndClass.functionClassName;

		HeaderClassDeclaration functionClass = null; //psi element that links to the function's class declaration
		HeaderClassDeclaration classWithTag = null; //class with tag as class name

		Attribute[] tagsAsAttributes = {new Attribute("tag", "\"" + tagName + "\"")};

		ArrayList<HeaderClassDeclaration> matchedClassesWithTag = (getClassDeclarationsWithEntriesEqual(cfgFuncs, null, tagsAsAttributes, true, 1, 1)); //classes inside CfgFunctions that have tag attribute

		if (matchedClassesWithTag.size() == 0) { //no classes of depth 1 relative to CfgFunctions have tag attribute, so find the class itself with its className=tagName
			classWithTag = getClassDeclaration(cfgFuncs, tagName, true, 1, 1);
			if (classWithTag == null) {
				throw new FunctionNotDefinedInConfigException(functionName);
			}

			functionClass = getClassDeclaration(classWithTag, functionClassName, true, 2, 2); //get the first function class
			if (functionClass == null) {
				throw new FunctionNotDefinedInConfigException(functionName);
			}
		} else {
			for (HeaderClassDeclaration matchedTagClass : matchedClassesWithTag) {
				functionClass = getClassDeclaration(matchedTagClass, functionClassName, true, 2, 2); //check if the class that has the tag actually holds the function's class declaration
				if (functionClass != null) {
					classWithTag = matchedTagClass;
					break;
				}
			}
			if (functionClass == null) {
				throw new FunctionNotDefinedInConfigException(functionName);
			}
		}

		return getHeaderConfigFunction(cfgFuncs, tagName, classWithTag, functionClass);
	}

	/**
	 * Gets a function's details from an already known header class declaration
	 *
	 * @param cfgFuncs      CfgFunction class declaration
	 * @param tagName       tag name of the function
	 * @param functionClass class declaration for the function
	 * @return the HeaderConfigFunction instance representing this function
	 */
	@NotNull
	private static HeaderConfigFunction getHeaderConfigFunction(@NotNull HeaderClassDeclaration cfgFuncs, @NotNull String tagName, @NotNull HeaderClassDeclaration tagClass, @NotNull HeaderClassDeclaration functionClass) throws
			MalformedConfigException,
			FunctionNotDefinedInConfigException {
		String containingDirectoryPath = null; //file path (directories)
		String functionFileExtension = null; //file extension (.sqf, .fsm)
		boolean appendFn_ = true; //append fn_ to file name

		Attribute[] functionClassAttributes = functionClass.getAttributes(true); //inner attributes of the function's class declaration
		for (Attribute attribute : functionClassAttributes) { //read the function's inner attributes
			if (attribute.name.equals("file")) {
				if (attribute.value.contains("\\")) {//file path includes a folder
					containingDirectoryPath = attribute.value.substring(1, attribute.value.lastIndexOf('\\')); //has a folder (file='folder\test.sqf'). Also, substring must start at 1 to skip over first "
				} else { //just the file name is inside
					containingDirectoryPath = ""; //no path defined, just the file name (file='test.sqf')
				}
				appendFn_ = false;
				if (isAllowedFunctionExtension(attribute.value, cfgFuncs, functionClass)) {
					functionFileExtension = attribute.value.substring(attribute.value.lastIndexOf('.'), attribute.value.length() - 1); //must be length -1 because ending quote
				}
				break;
			}
			if (attribute.name.equals("ext")) {
				if (isAllowedFunctionExtension(attribute.value, cfgFuncs, functionClass)) {
					functionFileExtension = stripOuterQuotes(attribute.value);
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
						containingDirectoryPath = stripOuterQuotes(attribute.value);
						filePathDefined = true;
						break;
					}
				}
				if (!filePathDefined) {
					containingDirectoryPath = "functions\\" + containingClassForFunctionClass.getClassName();
				}
			} else {
				throw new FunctionNotDefinedInConfigException(SQFStatic.getFullFunctionName(tagName, functionClass.getClassName()));
			}
		}

		return new HeaderConfigFunction(functionClass, containingDirectoryPath, tagName, tagClass, functionFileExtension, appendFn_);
	}


	/**
	 * Get a list of all functions in the CfgFunctions class
	 *
	 * @param file any PsiFile inside the current module
	 * @return list of functions
	 * @throws DescriptionExtNotDefinedException when Description.ext doesn't exist
	 * @throws ConfigClassNotDefinedException    when CfgFunctions doesn't exist
	 */
	public static ArrayList<HeaderConfigFunction> getAllConfigFunctionsFromDescriptionExt(PsiFile file) throws DescriptionExtNotDefinedException, ConfigClassNotDefinedException {
		HeaderClassDeclaration cfgFunc = getCfgFunctions(file);
		return getAllConfigFunctionsFromDescriptionExtBody(cfgFunc);
	}

	/**
	 * Get a list of all functions in the CfgFunctions class
	 *
	 * @param module the current module that contains description.ext
	 * @return list of functions
	 * @throws DescriptionExtNotDefinedException when Description.ext doesn't exist
	 * @throws ConfigClassNotDefinedException    when CfgFunctions doesn't exist
	 */
	public static ArrayList<HeaderConfigFunction> getAllConfigFunctionsFromDescriptionExt(Module module) throws DescriptionExtNotDefinedException, ConfigClassNotDefinedException {
		HeaderClassDeclaration cfgFunc = getCfgFunctions(module);
		return getAllConfigFunctionsFromDescriptionExtBody(cfgFunc);
	}

	@NotNull
	private static ArrayList<HeaderConfigFunction> getAllConfigFunctionsFromDescriptionExtBody(HeaderClassDeclaration cfgFunc) {
		ArrayList<HeaderClassDeclaration> tagClasses = getClassDeclarationsWithEntriesEqual(cfgFunc, null, null, true, 1, 1);
		ArrayList<HeaderConfigFunction> functions = new ArrayList<>();

		for (HeaderClassDeclaration tagClass : tagClasses) {
			String tag = null;
			Attribute[] attributes = tagClass.getAttributes(true);
			for (Attribute attribute : attributes) {
				if (attribute.name.equals("tag")) {
					tag = stripOuterQuotes(attribute.value);
				}
			}
			if (tag == null) {
				tag = tagClass.getClassName();
			}
			ArrayList<HeaderClassDeclaration> functionClasses = getClassDeclarationsWithEntriesEqual(tagClass, null, null, true, 2, 2);
			try {
				for (HeaderClassDeclaration functionClass : functionClasses) {
					functions.add(getHeaderConfigFunction(cfgFunc, tag, tagClass, functionClass));
				}
			} catch (Exception e) {
				e.printStackTrace();
				//this should never happen
			}
		}

		return functions;
	}


	private static boolean isAllowedFunctionExtension(String value, HeaderClassDeclaration cfgFuncs, HeaderClassDeclaration functionClass) throws MalformedConfigException {
		value = stripOuterQuotes(value);
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
	 * @throws DescriptionExtNotDefinedException when description.ext doesn't exist
	 * @throws ConfigClassNotDefinedException    where CfgFunctions isn't defined inside description.ext
	 */
	@NotNull
	public static HeaderClassDeclaration getCfgFunctions(PsiFile psiFile) throws DescriptionExtNotDefinedException, ConfigClassNotDefinedException {
		return getCfgFunctions(PluginUtil.getModuleForPsiFile(psiFile));
	}

	/**
	 * Get's the class declaration to CfgFunctions inside description.ext
	 *
	 * @param module module to retrieve the description.ext for.
	 * @return class declaration
	 * @throws DescriptionExtNotDefinedException when description.ext doesn't exist
	 * @throws ConfigClassNotDefinedException    where CfgFunctions isn't defined inside description.ext
	 */
	@NotNull
	public static HeaderClassDeclaration getCfgFunctions(Module module) throws DescriptionExtNotDefinedException, ConfigClassNotDefinedException {
		HeaderFile file = ArmaProjectDataManager.getInstance().getDataForModule(module).getDescriptionExt();
		HeaderClassDeclaration cfgFunc = getClassDeclaration(file, "CfgFunctions", true, 1, 1);
		if (cfgFunc == null) {
			throw new ConfigClassNotDefinedException("CfgFunctions");
		}
		return cfgFunc;
	}

	/**
	 * Creates and returns a new HeaderClassDeclaration PsiElement with the given class name and attributes
	 *
	 * @param project    project
	 * @param className  class name of the class
	 * @param attributes all attributes (null if there is none). Attributes are assignments (basic or array)
	 * @return new HeaderClassDeclaration PsiElement
	 */
	public static HeaderClassDeclaration createClassDeclaration(@NotNull Project project, @NotNull String className, @Nullable Attribute[] attributes) {
		String class_decl_f = "class %s {%s};";
		String attribute_f = "%s=%s;\n";
		String attributeText = "";
		if (attributes != null) {
			for (Attribute attribute : attributes) {
				attributeText += String.format(attribute_f, attribute.name, attribute.value);
			}
		}
		return (HeaderClassDeclaration) createElement(project, String.format(class_decl_f, className, attributeText), HeaderTypes.CLASS_DECLARATION);
	}

	@NotNull
	public static HeaderFile createFile(@NotNull Project project, @NotNull String text) {
		String fileName = "fake_header_file.sqf";
		return (HeaderFile) PsiFileFactory.getInstance(project).createFileFromText(fileName, HeaderFileType.INSTANCE, text);
	}

	public static HeaderFileEntry createFileEntryForClass(@NotNull Project project, @NotNull HeaderClassDeclaration classDeclaration) {
		return (HeaderFileEntry) createElement(project, classDeclaration.getText() + ";", HeaderTypes.FILE_ENTRY);
	}

	@NotNull
	public static PsiElement createElement(@NotNull Project project, @NotNull String text, @NotNull IElementType type) {
		HeaderFile file = createFile(project, text);
		return PsiUtil.findDescendantElements(file, type, null).get(0).getPsi();
	}

	private static String stripOuterQuotes(String s) {
		if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
			s = s.substring(1, s.length() - 1);
		}
		return s;
	}

}
