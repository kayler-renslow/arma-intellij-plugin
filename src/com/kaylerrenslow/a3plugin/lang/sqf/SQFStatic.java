package com.kaylerrenslow.a3plugin.lang.sqf;

import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.util.FileReader;
import com.kaylerrenslow.a3plugin.util.ResourceGetter;
import com.kaylerrenslow.a3plugin.util.TextFileListToList;
import com.kaylerrenslow.a3plugin.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kayler on 12/28/2015.
 */
public class SQFStatic{
	public static final String NAME = Plugin.resources.getString("lang.sqf.name");
	public static final String DESCRIPTION = Plugin.resources.getString("lang.sqf.description");
	public static final String FILE_EXTENSION = Plugin.resources.getString("lang.sqf.file_extension");
	public static final String FILE_EXTENSION_DEFAULT = Plugin.resources.getString("lang.sqf.file_extension_default");

	public static final String COMMANDS_DOC_FILE_DIR = "/com/kaylerrenslow/a3plugin/lang/sqf/commands-doc/";

	public static final List<String> LIST_COMMANDS = TextFileListToList.appendFileNamesToList(Util.convertURLToFile(ResourceGetter.getResourceAsURL(COMMANDS_DOC_FILE_DIR)), new ArrayList<>(), false);

	public static final String SQF_SAMPLE_CODE_TEXT = FileReader.getText("/com/kaylerrenslow/a3plugin/lang/sqf/codeStyle/sqfSampleCode.sqf");
}
