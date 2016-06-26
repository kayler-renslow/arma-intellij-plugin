package com.kaylerrenslow.a3plugin.lang.header.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderStringtableKey;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import com.kaylerrenslow.a3plugin.lang.header.psi.references.HeaderStringtableKeyReference;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.Stringtable;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.StringtableKey;
import com.kaylerrenslow.a3plugin.project.ArmaProjectDataManager;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;

/**
 @author Kayler
 Mixin class for Header stringtable key entry ($STR_something_in_str_table)
 Created on 06/25/2016.
 */
public abstract class HeaderStringtableKeyMixin extends ASTWrapperPsiElement implements PsiNamedElement, HeaderStringtableKey {
	public HeaderStringtableKeyMixin(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public PsiReference getReference() {
		return getReferences()[0];
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		Module m = PluginUtil.getModuleForPsiFile(getContainingFile());
		if (m == null) {
			return PsiReference.EMPTY_ARRAY;
		}
		Stringtable stringtable;
		try {
			stringtable = ArmaProjectDataManager.getInstance().getDataForModule(m).getStringtable();
		} catch (FileNotFoundException e) {
			return PsiReference.EMPTY_ARRAY;
		}
		String nonquote = getKey();
		StringtableKey[] keysValues = stringtable.getAllKeysValues();
		for(StringtableKey key : keysValues){
			if(key.getKeyName().equals(nonquote)){
				return new PsiReference[]{new HeaderStringtableKeyReference(this, key.getKeyXmlValue())};
			}
		}
		return super.getReferences();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
		HeaderStringtableKey key = (HeaderStringtableKey) HeaderPsiUtil.createElement(getProject(), "dummy="+name.replaceFirst("[sS][tT][rR]_", "\\$STR_")+";", HeaderTypes.STRINGTABLE_KEY);
		getParent().getNode().replaceChild(this.getNode(), key.getNode());
		return key;
	}
}
