package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFVariableReference;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Kayler
 * PsiElement mixin for SQF grammar file. This mixin is meant for SQFVariables
 * Created on 03/19/2016.
 */
public class SQFVariableNamedElementMixin extends ASTWrapperPsiElement implements SQFVariableNamedElement{
	private final IElementType myVariableElementType;

	public SQFVariableNamedElementMixin(@NotNull ASTNode node) {
		super(node);
		this.myVariableElementType = this.getNode().getFirstChildNode().getElementType();
	}

	@Override
	public IElementType getVariableType(){
		return this.myVariableElementType;
	}

	@Override
	public ItemPresentation getPresentation() {
		PsiFile file = this.getContainingFile();
		PsiElement element = this;
		return new ItemPresentation(){
			@Nullable
			@Override
			public String getPresentableText() {
				return element.getText();
			}

			@Nullable
			@Override
			public String getLocationString() {
				return file.getName();
			}

			@Nullable
			@Override
			public Icon getIcon(boolean unused) {
				return PluginIcons.ICON_SQF_VARIABLE;
			}
		};
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		if(myVariableElementType == SQFTypes.LANG_VAR){
			return new PsiReference[]{getReference()};
		}
		PsiReference[] references= ReferenceProvidersRegistry.getReferencesFromProviders(this);
		return ArrayUtil.prepend(getReference(), references);
	}


	@Override
	public PsiReference getReference() {
		return new SQFVariableReference(this, myVariableElementType);
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier() {
		return this.getNode().getPsi();
	}

	@Override
	public String getName() {
		return getNode().getText();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
		if(myVariableElementType == SQFTypes.LANG_VAR){
			throw new IncorrectOperationException("This variable can not be renamed.");
		}
		ASTNode me = this.getNode();
		SQFVariable var = SQFPsiUtil.createVariable(this.getProject(), name);
		me.getTreeParent().replaceChild(me, var.getNode());
		return var;
	}

	@Override
	public String getVarName() {
		return this.getName();
	}
}
