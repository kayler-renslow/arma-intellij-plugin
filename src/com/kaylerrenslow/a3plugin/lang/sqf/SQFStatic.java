package com.kaylerrenslow.a3plugin.lang.sqf;

import com.intellij.openapi.util.Pair;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.util.FileReader;
import com.kaylerrenslow.a3plugin.util.ResourceGetter;
import com.kaylerrenslow.a3plugin.util.TextFileListToList;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * Created on 12/28/2015.
 */
public class SQFStatic{
	public static final String NAME = "Arma.SQF";
	public static final String NAME_FOR_DISPLAY = Plugin.resources.getString("lang.sqf.name_for_display");
	public static final String DESCRIPTION = Plugin.resources.getString("lang.sqf.description");
	public static final String FILE_EXTENSION = Plugin.resources.getString("lang.sqf.file_extension");
	public static final String FILE_EXTENSION_DEFAULT = Plugin.resources.getString("lang.sqf.file_extension_default");

	public static final String COMMANDS_DOC_FILE_DIR = "/com/kaylerrenslow/a3plugin/lang/sqf/raw_doc/commands-doc/";
	public static final String BIS_FUNCTIONS_DOC_FILE_DIR = "/com/kaylerrenslow/a3plugin/lang/sqf/raw_doc/bis-functions-doc/";

	public static final List<String> LIST_COMMANDS = TextFileListToList.appendFileNamesToList(PluginUtil.convertURLToFile(ResourceGetter.getResourceAsURL(COMMANDS_DOC_FILE_DIR)), new ArrayList<>(), false);
	public static final List<String> LIST_FUNCTIONS = TextFileListToList.appendFileNamesToList(PluginUtil.convertURLToFile(ResourceGetter.getResourceAsURL(BIS_FUNCTIONS_DOC_FILE_DIR)), new ArrayList<>(), false);

	public static final String SQF_SAMPLE_CODE_TEXT = FileReader.getText("/com/kaylerrenslow/a3plugin/lang/sqf/codeStyle/sqfSampleCode.sqf");

	public static final String FUNCTION_NAMING_RULE_REGEX = "[a-zA-z_0-9]+_fnc_[a-zA-z_0-9]+"; //don't need to check if the function name starts with a number since that is asserted with the lexer

	/** Fetch command syntax for given command. This method will fetch the syntax and params from file and make it readable. Example: "paramName COMMAND paramName2" to "paramName:Number COMMAND paramName2:Number"
	 * @param command command String name
	 * @return syntax with params and param types, or null if the command doesn't have a known syntax
	 */
	@Nullable
	public static String getCommandDocSyntax(@NotNull String command){
		String path = COMMANDS_DOC_FILE_DIR + "syntax/" + command + ".param.list";
		List<String> paramsList = new ArrayList<>();
		URL url = ResourceGetter.getResourceAsURL(path);
		if(url == null){
			return null;
		}
		TextFileListToList.getListFromFile(PluginUtil.convertURLToFile(url), paramsList);
		String syntax = paramsList.get(0);
		for(int i = 1; i < paramsList.size(); i++){
			String detailedParam = paramsList.get(i);
			int indexDash = detailedParam.indexOf('-');
			if(indexDash > 0){
				detailedParam = detailedParam.substring(0, indexDash);
			}
			String paramName = detailedParam.substring(0, detailedParam.indexOf(':'));
			syntax = syntax.replace(paramName.trim(), detailedParam);

		}

		return syntax;
	}

	static{
		Collections.sort(LIST_COMMANDS);
		Collections.sort(LIST_FUNCTIONS);
	}

	/** Parses a full function name (e.g. tag_fnc_functionClass) and returns a pair containing the tag name and function class name. Pair first = tag, pair second = function class name
	 * @param fullFunctionName full function name
	 * @return SQFFunctionTagAndName instance
	 */
	public static SQFFunctionTagAndName getFunctionTagAndName(String fullFunctionName){
		int _fnc_Index = fullFunctionName.indexOf("_fnc_");
		String tagName = fullFunctionName.substring(0, _fnc_Index); //the function's prefix tag. exampleTag_fnc_functionClassName
		String functionClassName = fullFunctionName.substring(_fnc_Index + 5); //function's class name.
		return new SQFFunctionTagAndName(tagName, functionClassName);
	}

	/** Takes a tag and class name and returns the full SQF callable function name (e.g. tag_fnc_className)
	 * @param tag tag
	 * @param functionClassName class name
	 * @return full callable function name
	 */
	public static String getFullFunctionName(String tag, String functionClassName){
		return tag + "_fnc_" + functionClassName;
	}

	/** Returns the file name for the given sqf config function class name
	 * @param functionClassName name to get file name for
	 * @return fn_functionClassName.sqf
	 */
	public static String getConfigFunctionFileName(String functionClassName){
		return "fn_" + functionClassName + ".sqf";
	}

	public static class SQFFunctionTagAndName{
		public final String tagName;
		public final String functionClassName;

		public SQFFunctionTagAndName(String tagName, String functionClassName) {
			this.tagName = tagName;
			this.functionClassName = functionClassName;
		}
	}
}
