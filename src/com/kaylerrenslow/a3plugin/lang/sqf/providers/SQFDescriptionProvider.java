package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Kayler on 03/23/2016.
 */
public class SQFDescriptionProvider implements ElementDescriptionProvider{
	@Nullable
	@Override
	public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
		return null;
	}
}
