package com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation;

import com.intellij.navigation.ItemPresentation;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPrivateDeclVar;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Kayler
 * ItemPresentation for private declared vars. (private "_var")
 * Created on 04/08/2016.
 */
public class SQFPrivateDeclVarItemPresentation implements ItemPresentation {
	private final SQFPrivateDeclVar var;

	public SQFPrivateDeclVarItemPresentation(SQFPrivateDeclVar var) {
		this.var = var;
	}

	@Nullable
	@Override
	public String getPresentableText() {
		return var.getVarName();
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
