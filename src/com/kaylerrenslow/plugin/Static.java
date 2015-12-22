package com.kaylerrenslow.plugin;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by Kayler on 10/31/2015.
 */
public class Static {
    public static final Icon ICON_FILE = IconLoader.getIcon("/com/kaylerrenslow/plugin/icons/icon.png"); //http://www.jetbrains.org/intellij/sdk/docs/reference_guide/work_with_icons_and_images.html

    public static final String NAME = "SQF";
    public static final String DESCRIPTION = "SQF language file";
    public static final String FILE_EXTENSION = "sqf"; //NOTE. to have multiple file extensions, add ; (sqf;sqs for exmaple)
    public static final String FILE_EXTENSION_DEFAULT = "." + FILE_EXTENSION;
}
