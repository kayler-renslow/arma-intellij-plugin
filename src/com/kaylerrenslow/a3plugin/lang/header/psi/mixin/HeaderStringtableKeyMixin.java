package com.kaylerrenslow.a3plugin.lang.header.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderStringtableKey;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 Created by Kayler on 06/25/2016.
 */
public class HeaderStringtableKeyMixin extends ASTWrapperPsiElement implements PsiNamedElement {
	public HeaderStringtableKeyMixin(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
		HeaderStringtableKey key = (HeaderStringtableKey) HeaderPsiUtil.createElement(getProject(), "$STR_" + name, HeaderTypes.STRINGTABLE_KEY);
		getParent().getNode().replaceChild(this.getNode(), key.getNode());
		return key;
	}
}
