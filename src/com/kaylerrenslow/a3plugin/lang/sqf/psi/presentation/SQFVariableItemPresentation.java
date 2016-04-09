package com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation;

import com.intellij.navigation.ItemPresentation;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Kayler
 * ItemPresentation for any SQFVariable. This includes local variables, global variables (that are not defined for functions. Use SQFFunctionItemPresentation for functions.), and magic variables
 * Created on 04/08/2016.
 */
public class SQFVariableItemPresentation implements ItemPresentation {
	private final SQFVariable var;

	public SQFVariableItemPresentation(SQFVariable var) {
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
		return PluginIcons.ICON_SQF_VARIABLE;
	}
}
