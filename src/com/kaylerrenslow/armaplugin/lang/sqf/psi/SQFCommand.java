package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFCommand extends ASTWrapperPsiElement {
	public SQFCommand(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	public String getCommandName() {
		return getNode().getText();
	}
}
