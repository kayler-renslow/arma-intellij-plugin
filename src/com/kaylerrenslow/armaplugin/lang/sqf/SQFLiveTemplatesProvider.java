package com.kaylerrenslow.armaplugin.lang.sqf;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * @since 05/07/2016
 */
public class SQFLiveTemplatesProvider implements DefaultLiveTemplatesProvider {
	@Override
	public String[] getDefaultLiveTemplateFiles() {
		final String path = "com/kaylerrenslow/armaplugin/liveTemplates/sqf/";
		return new String[]{path + "SQFParamsD",
				path + "SQFCall",
				path + "SQFForSpec",
				path + "SQFForVar",
				path + "SQFIfThen",
				path + "SQFIfExit",
				path + "SQFHintArg",
				path + "SQFHintValue"
		};
	}

	@Nullable
	@Override
	public String[] getHiddenLiveTemplateFiles() {
		return new String[0];
	}
}
