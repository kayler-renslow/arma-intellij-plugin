package com.kaylerrenslow.armaplugin.lang.header;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.kaylerrenslow.armaplugin.ArmaPlugin;
import com.kaylerrenslow.armaplugin.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * LanguageFileType extension point for Header language. This data is viewable through the window: File -> Settings -> Editor -> File Types
 *
 * @author Kayler
 * @since 10/31/2015
 */
public class HeaderFileType extends LanguageFileType {
	public static final HeaderFileType INSTANCE = new HeaderFileType();

	public HeaderFileType() {
		super(HeaderLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public String getName() {
		return "Arma.Header";
	}

	@NotNull
	@Override
	public String getDescription() {
		return ArmaPlugin.getPluginBundle().getString("HeaderFileType.description");
	}

	@NotNull
	@Override
	public String getDefaultExtension() {
		return ".h";
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return PluginIcons.ICON_HEADER;
	}


}
