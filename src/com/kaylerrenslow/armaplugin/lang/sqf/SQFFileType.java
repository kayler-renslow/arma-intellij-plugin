package com.kaylerrenslow.armaplugin.lang.sqf;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.kaylerrenslow.armaplugin.ArmaPlugin;
import com.kaylerrenslow.armaplugin.ArmaPluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * FileType for SQF language. This data is viewable through the window: File -> Settings -> Editor -> File Types
 *
 * @author Kayler
 * @since 10/31/2015
 */
public class SQFFileType extends LanguageFileType {
	public static final SQFFileType INSTANCE = new SQFFileType();

	public SQFFileType() {
		super(SQFLanguage.INSTANCE);
	}


	@NotNull
	@Override
	public String getName() {
		return "Arma.SQF";
	}

	@NotNull
	@Override
	public String getDescription() {
		return ArmaPlugin.getPluginBundle().getString("SQFFileType.description");
	}

	@NotNull
	@Override
	public String getDefaultExtension() {
		return ".sqf";
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return ArmaPluginIcons.ICON_SQF;
	}


}
