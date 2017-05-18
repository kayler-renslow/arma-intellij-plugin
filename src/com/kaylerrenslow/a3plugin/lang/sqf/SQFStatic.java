package com.kaylerrenslow.a3plugin.lang.sqf;

import com.kaylerrenslow.a3plugin.util.TextFileListToList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 12/28/2015
 */
public class SQFStatic {
	static final String NAME = "Arma.SQF";
	//	public static final String NAME_FOR_DISPLAY = Plugin.resources.getString("lang.sqf.name_for_display");
	public static final String DESCRIPTION = "SQFStatic.description";
	static final String FILE_EXTENSION = "sqf";
	static final String FILE_EXTENSION_DEFAULT = ".sqf";

	private static final String COMMANDS_DOC_FILE_DIR = "/com/kaylerrenslow/a3plugin/lang/sqf/raw_doc/commands-doc/";
	private static final String BIS_FUNCTIONS_DOC_FILE_DIR = "/com/kaylerrenslow/a3plugin/lang/sqf/raw_doc/bis-functions-doc/";

	private static final String COMMANDS_DOC_FILE_LOOKUP = COMMANDS_DOC_FILE_DIR + "lookup.list";
	private static final String BIS_FUNCTIONS_DOC_FILE_LOOKUP = BIS_FUNCTIONS_DOC_FILE_DIR + "lookup.list";

	private static final String BIS_WIKI_URL_PREFIX = "https://community.bistudio.com/wiki/";
	private static final String EXTERNAL_LINK_NOTIFICATION = "<b>Online Wiki link: <a href='%1$s' style='color:008800'>%1$s</a></b><p>Green links are external links.</p>";


	/**
	 * Has all commands stored in their camelCase form
	 */
	public static final List<String> LIST_COMMANDS = TextFileListToList.getListFromStream(SQFStatic.class.getResourceAsStream(COMMANDS_DOC_FILE_LOOKUP), new ArrayList<>());
	public static final List<String> LIST_BIS_FUNCTIONS = TextFileListToList.getListFromStream(SQFStatic.class.getResourceAsStream(BIS_FUNCTIONS_DOC_FILE_LOOKUP), new ArrayList<>());

	private static final String FUNCTION_NAMING_RULE_REGEX = "[a-zA-z_0-9]+_fnc_[a-zA-z_0-9]+"; //don't need to check if the function name starts with a number since that is asserted with the lexer

	static {
		Collections.sort(LIST_COMMANDS);
		Collections.sort(LIST_BIS_FUNCTIONS);
	}


	/**
	 * Fetch command syntax for given command. This method will fetch the syntax and params from file and make it readable. Example: "paramName COMMAND paramName2" to "paramName:Number COMMAND paramName2:Number"
	 *
	 * @param command command String name
	 * @return syntax with params and param types, or null if the command doesn't have a known syntax
	 */
	@Nullable
	@Deprecated
	public static String getCommandDocSyntax(@NotNull String command) {
		//		String path = COMMANDS_DOC_FILE_DIR + "syntax/" + command + ".param.list";
		//		List<String> paramsList = new ArrayList<>();
		//		URL url = ResourceGetter.getResourceAsURL(path);
		//		if(url == null){
		//			return null;
		//		}
		//		TextFileListToList.getListFromFile(PluginUtil.convertURLToFile(url), paramsList);
		//		String syntax = paramsList.get(0);
		//		for(int i = 1; i < paramsList.size(); i++){
		//			String detailedParam = paramsList.get(i);
		//			int indexDash = detailedParam.indexOf('-');
		//			if(indexDash > 0){
		//				detailedParam = detailedParam.substring(0, indexDash);
		//			}
		//			String paramName = detailedParam.substring(0, detailedParam.indexOf(':'));
		//			syntax = syntax.replace(paramName.trim(), detailedParam);
		//
		//		}
		//
		//		return syntax;
		return null;
	}


	/**
	 * Parses a full function name (e.g. tag_fnc_functionClass) and returns a pair containing the tag name and function class name. Pair first = tag, pair second = function class name
	 *
	 * @param fullFunctionName full function name
	 * @return SQFFunctionTagAndName instance
	 * @throws IllegalArgumentException when the function name doesn't follow the function naming requirements
	 */
	@NotNull
	public static SQFFunctionTagAndName getFunctionTagAndName(String fullFunctionName) {
		if (!SQFStatic.followsSQFFunctionNameRules(fullFunctionName)) {
			throw new IllegalArgumentException("function '" + fullFunctionName + "' doesn't follow SQF function name rules.");
		}
		int _fnc_Index = fullFunctionName.indexOf("_fnc_");
		String tagName = fullFunctionName.substring(0, _fnc_Index); //the function's prefix tag. exampleTag_fnc_functionClassName
		String functionClassName = fullFunctionName.substring(_fnc_Index + 5); //function's class name.
		return new SQFFunctionTagAndName(tagName, functionClassName);
	}

	@NotNull
	public static SQFFunctionTagAndName getFunctionTagAndName(@NotNull SQFVariableName fullFunctionName) {
		return getFunctionTagAndName(fullFunctionName.textOriginal());
	}

	/**
	 * Takes a tag and class name and returns the full SQF callable function name (e.g. tag_fnc_className)
	 *
	 * @param tag               tag
	 * @param functionClassName class name
	 * @return full callable function name
	 */
	public static String getFullFunctionName(String tag, String functionClassName) {
		return tag + "_fnc_" + functionClassName;
	}

	/**
	 * Returns the file name for the given sqf config function class name
	 *
	 * @param functionClassName name to get file name for
	 * @return fn_functionClassName.sqf
	 */
	public static String getConfigFunctionFileName(String functionClassName) {
		return "fn_" + functionClassName + ".sqf";
	}

	/**
	 * Checks if the given variable name follows the general rules of function naming (requires tag, _fnc_ and then an identifier).
	 * <p>Examples: tag_fnc_function, sj_fnc_function2</p>
	 * <p>Counter Examples: tag_fn_c_function, sj_nc_function2, potatoes, _fnc_function</p>
	 *
	 * @param variable Variable to test
	 * @return true if matches, false if it doesn't
	 */
	public static boolean followsSQFFunctionNameRules(@NotNull String variable) {
		return variable.matches(FUNCTION_NAMING_RULE_REGEX); //don't need to explicitly check if a number starts the variable name since that is asserted by the lexer
	}

	public static boolean followsSQFFunctionNameRules(@NotNull SQFVariableName name) {
		return followsSQFFunctionNameRules(name.text());
	}

	/**
	 * Return true if the given var name is a BIS function, false if it isn't.
	 */
	public static boolean isBisFunction(String varName) {
		if (!varName.toLowerCase().startsWith("bis_fnc_")) { //do a quick check instead of always doing a bin search.
			return false;
		}
		return Collections.binarySearch(LIST_BIS_FUNCTIONS, varName) >= 0;
	}


	public static class SQFFunctionTagAndName {
		public final String tagName;
		public final String functionClassName;

		public SQFFunctionTagAndName(String tagName, String functionClassName) {
			this.tagName = tagName;
			this.functionClassName = functionClassName;
		}
	}
}
