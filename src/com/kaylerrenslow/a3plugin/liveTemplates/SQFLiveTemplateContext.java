package com.kaylerrenslow.a3plugin.liveTemplates;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Kayler on 05/07/2016.
 */
public class SQFLiveTemplateContext extends TemplateContextType {
	private static final String ID = "SQF_CONTEXT";
	private static final String NAME = "SQF";

	protected SQFLiveTemplateContext() {
		super(ID, NAME);
	}

	@Override
	public boolean isInContext(@NotNull PsiFile file, int offset) {
		return file instanceof SQFFile;
	}
}
