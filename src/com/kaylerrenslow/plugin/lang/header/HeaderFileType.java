package com.kaylerrenslow.plugin.lang.header;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.kaylerrenslow.plugin.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by Kayler on 10/31/2015.
 */
public class HeaderFileType extends LanguageFileType{
	public static final HeaderFileType INSTANCE = new HeaderFileType();

	public HeaderFileType() {
		super(HeaderLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public String getName() {
		return HeaderStatic.NAME;
	}

	@NotNull
	@Override
	public String getDescription() {
		return HeaderStatic.DESCRIPTION;
	}

	@NotNull
	@Override
	public String getDefaultExtension() {
		return HeaderStatic.FILE_EXTENSION_DEFAULT;
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return PluginIcons.ICON_FILE;
	}



}
