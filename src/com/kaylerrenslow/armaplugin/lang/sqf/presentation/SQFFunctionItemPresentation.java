package com.kaylerrenslow.armaplugin.lang.sqf.presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.armaplugin.ArmaPluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 * ItemPresentation for config defined functions
 *
 * @author Kayler
 * @since 04/08/2016
 */
public class SQFFunctionItemPresentation implements ItemPresentation {
	private final String varName;
	private final PsiFile ownerFile;

	public SQFFunctionItemPresentation(@NotNull String varName, @NotNull PsiFile ownerFile) {
		this.varName = varName;
		this.ownerFile = ownerFile;
	}

	@Nullable
	@Override
	public String getPresentableText() {
		return this.varName;
	}

	@Nullable
	@Override
	public String getLocationString() {
		return this.ownerFile.getName();
	}

	@Nullable
	@Override
	public Icon getIcon(boolean unused) {
		return ArmaPluginIcons.ICON_SQF_FUNCTION;
	}
}
