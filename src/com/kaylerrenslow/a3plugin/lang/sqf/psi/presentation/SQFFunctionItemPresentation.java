package com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderConfigFunction;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Kayler
 * ItemPresentation for config defined functions
 * Created on 04/08/2016.
 */
public class SQFFunctionItemPresentation implements ItemPresentation {
	private final String varName;
	private final PsiFile file;

	public SQFFunctionItemPresentation(String varName, PsiFile file) {
		this.varName = varName;
		this.file = file;
	}

	@Nullable
	@Override
	public String getPresentableText() {
		return this.varName;
	}

	@Nullable
	@Override
	public String getLocationString() {
		return this.file.getName();
	}

	@Nullable
	@Override
	public Icon getIcon(boolean unused) {
		return HeaderConfigFunction.getIcon();
	}
}
