package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 03/28/2016.
 */
public class SQFDefaultLiveTemplatesProvider implements DefaultLiveTemplatesProvider{
	@Override
	public String[] getDefaultLiveTemplateFiles() {
		return new String[]{"liveTemplates/sqfFileTemplate.sqf"};
	}

	@Nullable
	@Override
	public String[] getHiddenLiveTemplateFiles() {
		return new String[0];
	}
}
