package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Kayler on 03/28/2016.
 */
public class SQFLiveTemplateContext extends TemplateContextType{
	protected SQFLiveTemplateContext() {
		super("SQF", "SQF presentable name");
	}

	@Override
	public boolean isInContext(@NotNull PsiFile file, int offset) {
		return file.getName().endsWith(".sqf");
	}
}
