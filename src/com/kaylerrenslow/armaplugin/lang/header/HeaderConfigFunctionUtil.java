package com.kaylerrenslow.armaplugin.lang.header;

import com.kaylerrenslow.armaDialogCreator.arma.header.*;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kayler
 * @since 09/09/2017
 */
public class HeaderConfigFunctionUtil {
	/**
	 * Get's the {@link HeaderClass} to CfgFunctions inside a root config file
	 *
	 * @param rootConfigHeaderFile the {@link HeaderFile} instance that is the root config for CfgFunctions
	 * @return class declaration
	 * @throws ConfigClassNotDefinedException where CfgFunctions isn't defined inside description.ext
	 */
	@NotNull
	public static HeaderClass getCfgFunctions(@NotNull HeaderFile rootConfigHeaderFile) throws ConfigClassNotDefinedException {
		HeaderClass cfgFunctions = rootConfigHeaderFile.getClasses().getByName("CfgFunctions", false);
		if (cfgFunctions == null) {
			throw new ConfigClassNotDefinedException("CfgFunctions");
		}
		return cfgFunctions;
	}

	@NotNull
	public static List<HeaderConfigFunction> getAllConfigFunctionsFromRootConfig(@NotNull HeaderClass cfgFunc) {
		List<HeaderClass> tagClasses = getClassDeclarationsWithEntriesEqual(cfgFunc, null, null, new ArrayList<>(), 1, 1, 0);
		List<HeaderConfigFunction> functions = new ArrayList<>();

		for (HeaderClass tagClass : tagClasses) {
			String tag = null;
			List<HeaderAssignment> assignments = tagClass.getAssignmentsIncludingInherited(false);
			for (HeaderAssignment assignment : assignments) {
				if (assignment.getVariableName().equalsIgnoreCase("tag")) {
					tag = stripOuterQuotes(assignment.getValue().getContent());
					break;
				}
			}
			if (tag == null) {
				tag = tagClass.getClassName();
			}
			List<HeaderClass> functionClasses = getClassDeclarationsWithEntriesEqual(tagClass, null, null, new ArrayList<>(), 2, 2, 0);
			try {
				for (HeaderClass functionClass : functionClasses) {
					functions.add(getHeaderConfigFunction(cfgFunc, tag, tagClass, functionClass));
				}
			} catch (Exception e) {
				e.printStackTrace();
				//this should never happen
			}
		}

		return functions;
	}

	/**
	 * Gets a function from CfgFunctions. The function name must be the full name (i.e. tag_fnc_functionClass)
	 *
	 * @param functionName full function name (i.e. tag_fnc_functionClass)
	 * @param cfgFuncs     {@link HeaderClass} instance that has all the function declarations
	 * @return the function, or null if couldn't be found
	 * @throws FunctionNotDefinedInConfigException
	 * @throws MalformedConfigException
	 */
	@Nullable
	private static HeaderConfigFunction getFunctionFromCfgFunctionsBody(@NotNull SQFVariableName functionName,
																		@NotNull HeaderClass cfgFuncs)
			throws FunctionNotDefinedInConfigException, MalformedConfigException {
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
		String tagName = tagAndClass.getTagName();
		String functionClassName = tagAndClass.getFunctionClassName();

		HeaderClass functionClass = null; //psi element that links to the function's class declaration
		HeaderClass classWithTag = null; //class with tag as class name

		HeaderAssignment[] tagsAsAssignments = {new HeaderAssignmentImpl("tag", "\"" + tagName + "\"")};

		List<HeaderClass> matchedClassesWithTag = (getClassDeclarationsWithEntriesEqual(cfgFuncs, null, tagsAsAssignments, new ArrayList<>(), 1, 1, 0)); //classes inside CfgFunctions that have tag attribute

		if (matchedClassesWithTag.size() == 0) { //no classes of depth 1 relative to CfgFunctions have tag attribute, so find the class itself with its className=tagName
			classWithTag = getClassDeclaration(cfgFuncs, tagName, 1, 1);
			if (classWithTag == null) {
				throw new FunctionNotDefinedInConfigException(functionName.textOriginal());
			}

			functionClass = getClassDeclaration(classWithTag, functionClassName, 2, 2); //get the first function class
			if (functionClass == null) {
				throw new FunctionNotDefinedInConfigException(functionName.textOriginal());
			}
		} else {
			for (HeaderClass matchedTagClass : matchedClassesWithTag) {
				functionClass = getClassDeclaration(matchedTagClass, functionClassName, 2, 2); //check if the class that has the tag actually holds the function's class declaration
				if (functionClass != null) {
					classWithTag = matchedTagClass;
					break;
				}
			}
			if (functionClass == null) {
				throw new FunctionNotDefinedInConfigException(functionName.textOriginal());
			}
		}

		return getHeaderConfigFunction(cfgFuncs, tagName, classWithTag, functionClass);
	}

	/**
	 * Gets a function's details from an already known header class declaration
	 *
	 * @param cfgFuncs      CfgFunction class declaration
	 * @param tagName       tag name of the function
	 * @param classWithTag  the {@link HeaderClass} instance that owns the "tag" attribute/assignment
	 * @param functionClass class declaration for the function
	 * @return the HeaderConfigFunction instance representing this function
	 */
	@NotNull
	private static HeaderConfigFunction getHeaderConfigFunction(@NotNull HeaderClass cfgFuncs, @NotNull String tagName,
																@NotNull HeaderClass classWithTag, @NotNull HeaderClass functionClass)
			throws MalformedConfigException, FunctionNotDefinedInConfigException {
		String containingDirectoryPath = null; //file path (directories)
		String functionFileExtension = null; //file extension (.sqf, .fsm)
		String functionFileName = null; //function file name (test.sqf) or null if determined by function class name

		List<HeaderAssignment> functionClassAssignments = functionClass.getAssignmentsIncludingInherited(false); //inner attributes of the function's class declaration
		for (HeaderAssignment assignment : functionClassAssignments) { //read the function's inner attributes
			if (assignment.getVariableName().equalsIgnoreCase("file")) {
				String fileAttr = stripOuterQuotes(assignment.getValue().getContent());
				if (fileAttr.contains("\\")) {//file path includes a folder
					int lastSlash = fileAttr.lastIndexOf('\\');
					containingDirectoryPath = fileAttr.substring(0, lastSlash); //has a folder (file='folder\hello.sqf').
					functionFileName = fileAttr.substring(lastSlash + 1);
				} else { //just the file name is inside
					containingDirectoryPath = ""; //no path defined, just the file name (file='test.sqf')
					functionFileName = fileAttr.substring(0, fileAttr.lastIndexOf('.'));
				}

				if (isAllowedFunctionExtension(fileAttr, cfgFuncs, functionClass)) {
					functionFileExtension = fileAttr.substring(fileAttr.lastIndexOf('.'));
				}
				break;
			}
			if (assignment.getVariableName().equalsIgnoreCase("ext")) {
				if (isAllowedFunctionExtension(assignment.getValue().getContent(), cfgFuncs, functionClass)) {
					functionFileExtension = stripOuterQuotes(assignment.getValue().getContent());
				}
			}
		}

		if (containingDirectoryPath == null) { //file path is determined by containing class
			HeaderClass containingClassForFunctionClass = functionClass.getContainingClass();
			if (containingClassForFunctionClass != null) {
				List<HeaderAssignment> containingClassAssignments = containingClassForFunctionClass.getAssignmentsIncludingInherited(true);
				boolean filePathDefined = false;
				for (HeaderAssignment containingClassAssignment : containingClassAssignments) {
					if (containingClassAssignment.getVariableName().equalsIgnoreCase("file")) {
						containingDirectoryPath = stripOuterQuotes(containingClassAssignment.getValue().getContent());
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

		return new HeaderConfigFunction(functionClass, containingDirectoryPath,
				functionFileName, tagName, classWithTag, functionFileExtension
		);
	}

	/**
	 * Gets all {@link HeaderClass} instances that match the parameters
	 *
	 * @param startHeaderClass where to begin the search (will include this class if matches parameters)
	 * @param className        class name to match, or null if don't care
	 * @param assignments      assignments that must match, or null if don't care
	 * @param matchedClasses   list to insert results into
	 * @param minDepth         minimum search depth (use -1 to start at startHeaderClass)
	 * @param maxDepth         maximum search depth. Use {@link Integer#MAX_VALUE} if don't care
	 * @param currentDepth     the current depth. Should be >= 0
	 * @return matchedClasses
	 */
	private static List<HeaderClass> getClassDeclarationsWithEntriesEqual(@NotNull HeaderClass startHeaderClass, @Nullable String className,
																		  @Nullable HeaderAssignment[] assignments,
																		  @NotNull List<HeaderClass> matchedClasses,
																		  int minDepth, int maxDepth, int currentDepth) {
		if (currentDepth >= minDepth) {
			if (className == null || startHeaderClass.getClassName().equalsIgnoreCase(className)) {
				if (assignments == null || hasAssignments(startHeaderClass, assignments)) {
					matchedClasses.add(startHeaderClass);
				}
			}
		}
		if (currentDepth + 1 <= maxDepth) {
			for (HeaderClass nested : startHeaderClass.getNestedClassesIncludingInherited(false)) {
				getClassDeclarationsWithEntriesEqual(nested, className, assignments, matchedClasses, minDepth, maxDepth, currentDepth + 1);
			}
		}
		return matchedClasses;
	}

	private static boolean isAllowedFunctionExtension(@NotNull String value, @NotNull HeaderClass cfgFuncs, @NotNull HeaderClass functionClass) throws MalformedConfigException {
		value = stripOuterQuotes(value);
		if (value.endsWith(".sqf") || value.endsWith(".fsm")) {
			return true;
		}
		throw new MalformedConfigException(
				cfgFuncs.getOwnerFile(),
				String.format(HeaderStatic.getHeaderBundle().getString("file-type-isnt-sqf-or-fsm-f"), functionClass.getClassName())
		);
	}

	/**
	 * Traverses the PsiElement startElement to find the config class with its name=className
	 *
	 * @param start          where to start searching for a class declaration with the class declaration's name=className
	 * @param className      class name to search for
	 * @param minSearchDepth min depth to search for class declarations. must be >= 1
	 * @param maxSearchDepth max depth to search for class declarations. must be > 0
	 * @return HeaderClassDeclaration, or null if one couldn't be found
	 */
	@Nullable
	public static HeaderClass getClassDeclaration(@NotNull HeaderClass start, @NotNull String className, int minSearchDepth, int maxSearchDepth) {
		List<HeaderClass> decls = getClassDeclarationsWithEntriesEqual(start, className, null, new ArrayList<>(), minSearchDepth, maxSearchDepth, 0);

		if (decls.size() == 0) {
			return null;
		}
		return decls.get(0);
	}

	private static String stripOuterQuotes(String s) {
		if (s.length() >= 2) {
			if (s.charAt(0) == s.charAt(s.length() - 1) && (s.charAt(0) == '\'' || s.charAt(0) == '"')) {
				s = s.substring(1, s.length() - 1);
			}
		}
		return s;
	}

	private static boolean hasAssignments(@NotNull HeaderClass headerClass, @NotNull HeaderAssignment[] assignments) {
		List<HeaderAssignment> toMatch = new LinkedList<>();
		Collections.addAll(toMatch, assignments);
		for (HeaderAssignment classAssignment : headerClass.getAssignmentsIncludingInherited(false)) {
			toMatch.removeIf(assignment -> {
				return assignment.getVariableName().equalsIgnoreCase(assignment.getVariableName())
						&& classAssignment.getValue().equalsValue(assignment.getValue());
			});
		}

		return toMatch.isEmpty();
	}

	private static class HeaderAssignmentImpl implements HeaderAssignment {

		@NotNull
		private final String varName;
		@NotNull
		private final HeaderValue value;

		public HeaderAssignmentImpl(@NotNull String varName, @NotNull String value) {
			this.varName = varName;
			this.value = new BasicHeaderValue(value);
		}

		@Override
		@NotNull
		public String getVariableName() {
			return varName;
		}

		@Override
		@NotNull
		public HeaderValue getValue() {
			return value;
		}
	}
}
