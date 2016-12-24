package com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation;

import com.intellij.navigation.ItemPresentation;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFCommand;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * ItemPresentation for any command.
 *
 * @author Kayler
 * @since 06/06/2016
 */
public class SQFCommandItemPresentation implements ItemPresentation {
	private final SQFCommand command;

	public SQFCommandItemPresentation(SQFCommand command) {
		this.command = command;
	}

	@Nullable
	@Override
	public String getPresentableText() {
		return command.getText();
	}

	@Nullable
	@Override
	public String getLocationString() {
		return command.getContainingFile().getName();
	}

	@Nullable
	@Override
	public Icon getIcon(boolean unused) {
		return PluginIcons.ICON_SQF_COMMAND;
	}
}
