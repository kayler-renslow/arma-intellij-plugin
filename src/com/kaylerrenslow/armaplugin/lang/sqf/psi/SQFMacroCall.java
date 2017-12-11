package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 12/10/2017
 */
public class SQFMacroCall extends ASTWrapperPsiElement {
	public SQFMacroCall(@NotNull ASTNode node) {
		super(node);
	}
}
