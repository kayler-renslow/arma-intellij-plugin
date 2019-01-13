package com.kaylerrenslow.armaplugin.lang.sqf;

import com.kaylerrenslow.armaplugin.ArmaPlugin;
import com.kaylerrenslow.armaplugin.lang.header.HeaderStatic;
import com.kaylerrenslow.armaplugin.util.TextFileList;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;

/**
 * A bunch of utility methods and static fields for the SQF language
 *
 * @author Kayler
 * @since 12/28/2015
 */
public class SQFStatic {

	/**
	 * Get the ResourceBundle for the SQF language
	 *
	 * @see ArmaPlugin#getPluginBundle()
	 * @see HeaderStatic#getHeaderBundle()
	 */
	@NotNull
	public static ResourceBundle getSQFBundle() {
		return ResourceBundle.getBundle("com.kaylerrenslow.armaplugin.SQFBundle");
	}

	/**
	 * Source code package path to get to command documentation (append command name, as it appears in the wiki, to get documentation)
	 */
	@NotNull
	public static final String COMMANDS_DOC_FILE_DIR = "/com/kaylerrenslow/armaplugin/lang/sqf/raw_doc/commands-doc/";
	/**
	 * Source code package path to get to BIS function documentation (append function name, as it appears in the wiki, to get documentation)
	 */
	@NotNull
	public static final String BIS_FUNCTIONS_DOC_FILE_DIR = "/com/kaylerrenslow/armaplugin/lang/sqf/raw_doc/bis-functions-doc/";

	/**
	 * The file that contains all commands for SQF
	 */
	@NotNull
	private static final String COMMANDS_DOC_FILE_LOOKUP = COMMANDS_DOC_FILE_DIR + "lookup.list";
	/**
	 * The file that contains all BIS functions for SQF
	 */
	@NotNull
	private static final String BIS_FUNCTIONS_DOC_FILE_LOOKUP = BIS_FUNCTIONS_DOC_FILE_DIR + "lookup.list";

	/**
	 * Wiki URL prefix. Append a command name to get the wiki page
	 */
	@NotNull
	public static final String BIS_WIKI_URL_PREFIX = "https://community.bistudio.com/wiki/";



	/**
	 * Has all commands stored as presented in Wiki (usually camelCase)
	 */
	@NotNull
	public static final TreeSet<String> COMMANDS_SET = TextFileList.getTreeSetFromStream(SQFStatic.class.getResourceAsStream(COMMANDS_DOC_FILE_LOOKUP));
	/**
	 * Has all BIS functions stored as presented in Wiki
	 */
	@NotNull
	public static final List<String> LIST_BIS_FUNCTIONS = TextFileList.getListFromStream(SQFStatic.class.getResourceAsStream(BIS_FUNCTIONS_DOC_FILE_LOOKUP));

	@NotNull
	private static final String FUNCTION_NAMING_RULE_REGEX = "[a-zA-z_0-9]+_fnc_[a-zA-z_0-9]+"; //don't need to check if the function name starts with a number since that is asserted with the lexer

	static {
		Collections.sort(LIST_BIS_FUNCTIONS);
	}


	/**
	 * Parses a full function name (e.g. tag_fnc_functionClass) and returns a pair containing the tag name and function class name. Pair first = tag, pair second = function class name
	 *
	 * @param fullFunctionName full function name
	 * @return SQFFunctionTagAndName instance
	 * @throws IllegalArgumentException when the function name doesn't follow the function naming requirements
	 */
	@NotNull
	public static SQFFunctionTagAndName getFunctionTagAndName(@NotNull String fullFunctionName) {
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
	@NotNull
	public static String getFullFunctionName(@NotNull String tag, @NotNull String functionClassName) {
		return tag + "_fnc_" + functionClassName;
	}

	/**
	 * Returns the file name for the given sqf config function class name
	 *
	 * @param functionClassName name to get file name for
	 * @return fn_functionClassName.sqf
	 */
	@NotNull
	public static String getConfigFunctionFileName(@NotNull String functionClassName) {
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

	/**
	 * @return {@link #followsSQFFunctionNameRules(String)} with {@link SQFVariableName#text()} passed as argument
	 */
	public static boolean followsSQFFunctionNameRules(@NotNull SQFVariableName name) {
		return followsSQFFunctionNameRules(name.text());
	}

	/**
	 * This method will search {@link #LIST_BIS_FUNCTIONS} for the given BIS function name.
	 * Case sensitivity does not matter.
	 *
	 * @return true if the given var name is a BIS function, false if it isn't.
	 */
	public static boolean isBisFunction(@NotNull String varName) {
		if (!varName.toLowerCase().startsWith("bis_fnc_")) { //do a quick check instead of always doing a bin search.
			return false;
		}
		for (String bis : LIST_BIS_FUNCTIONS) {
			if (varName.equalsIgnoreCase(bis)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Helper class for passing a function's tag and function class name
	 */
	public static class SQFFunctionTagAndName {
		private final String tagName;
		private final String functionClassName;

		public SQFFunctionTagAndName(@NotNull String tagName, @NotNull String functionClassName) {
			this.tagName = tagName;
			this.functionClassName = functionClassName;
		}

		@NotNull
		public String getTagName() {
			return tagName;
		}

		@NotNull
		public String getFunctionClassName() {
			return functionClassName;
		}
	}
}
