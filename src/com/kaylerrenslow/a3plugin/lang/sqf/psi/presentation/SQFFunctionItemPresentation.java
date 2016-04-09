package com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation;

import com.intellij.navigation.ItemPresentation;
import com.kaylerrenslow.a3plugin.lang.header.psi.impl.HeaderConfigFunction;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Kayler
 * ItemPresentation for config defined functions
 * Created on 04/08/2016.
 */
public class SQFFunctionItemPresentation implements ItemPresentation {
	private final HeaderConfigFunction function;

	public SQFFunctionItemPresentation(HeaderConfigFunction function) {
		this.function = function;
	}

	@Nullable
	@Override
	public String getPresentableText() {
		return function.getCallableName();
	}

	@Nullable
	@Override
	public String getLocationString() {
		return function.getClassDeclaration().getContainingFile().getName();
	}

	@Nullable
	@Override
	public Icon getIcon(boolean unused) {
		return HeaderConfigFunction.getIcon();
	}
}
