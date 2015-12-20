package com.kaylerrenslow.plugin;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by Kayler on 10/31/2015.
 */
public class SQFFileType extends LanguageFileType{
	public static final SQFFileType INSTANCE = new SQFFileType();

	public SQFFileType() {
		super(SQFLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public String getName() {
		return Static.NAME;
	}

	@NotNull
	@Override
	public String getDescription() {
		return Static.DESCRIPTION;
	}

	@NotNull
	@Override
	public String getDefaultExtension() {
		return Static.FILE_EXTENSION_DEFAULT;
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return Static.ICON_FILE;
	}

}
