package com.kaylerrenslow.plugin.lang.sqf;

import com.kaylerrenslow.plugin.Plugin;
import com.kaylerrenslow.plugin.util.ResourceGetter;
import com.kaylerrenslow.plugin.util.TextFileListToList;
import com.kaylerrenslow.plugin.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kayler on 12/28/2015.
 */
public class SQFStatic{
	public static final String NAME = Plugin.resources.getString("lang.sqf.name");
	public static final String DESCRIPTION = Plugin.resources.getString("lang.sqf.description");
	public static final String FILE_EXTENSION = Plugin.resources.getString("lang.sqf.fileExtension");
	public static final String FILE_EXTENSION_DEFAULT = Plugin.resources.getString("lang.sqf.fileExtensionDefault");

	public static final List<String> LIST_COMMANDS = TextFileListToList.getListFromFile(Util.convertURLToFile(ResourceGetter.instance.getResource(Plugin.resources.getString("lang.sqf.commandsListFile"))), new ArrayList<>());
}
