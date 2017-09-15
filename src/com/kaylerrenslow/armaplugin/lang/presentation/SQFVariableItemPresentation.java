package com.kaylerrenslow.armaplugin.lang.presentation;

import com.intellij.navigation.ItemPresentation;
import com.kaylerrenslow.armaplugin.ArmaPluginIcons;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * ItemPresentation for any SQFVariable. This includes local variables, global variables (that are not defined for functions. Use SQFFunctionItemPresentation for functions.), and magic variables
 *
 * @author Kayler
 * @since 04/08/2016
 */
public class SQFVariableItemPresentation implements ItemPresentation {
	private final SQFVariable var;

	public SQFVariableItemPresentation(@NotNull SQFVariable var) {
		this.var = var;
	}

	@Nullable
	@Override
	public String getPresentableText() {
		return var.getText();
	}

	@Nullable
	@Override
	public String getLocationString() {
		return var.getContainingFile().getName();
	}

	@Nullable
	@Override
	public Icon getIcon(boolean unused) {
		return ArmaPluginIcons.ICON_SQF_VARIABLE;
	}
}
