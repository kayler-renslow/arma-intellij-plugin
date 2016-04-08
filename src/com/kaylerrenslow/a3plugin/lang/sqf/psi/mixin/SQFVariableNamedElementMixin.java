package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Kayler
 *         PsiElement mixin for SQF grammar file. This mixin is meant for SQFVariables
 *         Created on 03/19/2016.
 */
public class SQFVariableNamedElementMixin extends ASTWrapperPsiElement implements SQFVariableNamedElement {
	private final IElementType myVariableElementType;

	public SQFVariableNamedElementMixin(@NotNull ASTNode node) {
		super(node);
		this.myVariableElementType = this.getNode().getFirstChildNode().getElementType();
	}

	@Override
	public IElementType getVariableType() {
		return this.myVariableElementType;
	}

	@Override
	public ItemPresentation getPresentation() {
		PsiFile file = this.getContainingFile();
		PsiElement element = this;
		return new ItemPresentation() {
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

//	@NotNull
//	@Override
//	public PsiReference[] getReferences() {
//		if (myVariableElementType == SQFTypes.LANG_VAR) {
//			return new PsiReference[0];
//		}
//		PsiReference[] references;
//		if (myVariableElementType == SQFTypes.GLOBAL_VAR) {
//			references = ReferenceProvidersRegistry.getReferencesFromProviders(this);
//		} else {
//			SQFVariable me = ((SQFVariable) this);
//			SQFScope varScope = me.getDeclarationScope();
//			ArrayList<ASTNode> nodes = PsiUtil.findDescendantElements(me.getContainingFile(), SQFTypes.PRIVATE_DECL_VAR, null, "\"" + me.getVarName() + "\"");
//			ArrayList<PsiElement> declVarsMatchedScope = new ArrayList<>();
//			for (int i = 0; i < nodes.size(); i++) {
//				if (varScope == SQFPsiUtil.getContainingScope(nodes.get(i).getPsi())) {
//					declVarsMatchedScope.add(nodes.get(i).getPsi());
//				}
//			}
//
//			references = new PsiReference[declVarsMatchedScope.size()];
//			for (int i = 0; i < declVarsMatchedScope.size(); i++) {
//				references[i] = new SQFLocalVarReference(me, (SQFPrivateDeclVar) declVarsMatchedScope.get(i));
//			}
//		}
//		return references;
//	}
//
//	@Override
//	public PsiReference getReference() {
//		return getReferences()[0];
//	}

	@Override
	public String toString() {
		return "SQFVariableNamedElementMixin{" + this.getName() + "}";
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
		if (myVariableElementType == SQFTypes.LANG_VAR) {
			throw new IncorrectOperationException("This variable can not be renamed.");
		}
		SQFVariable newVar = SQFPsiUtil.createVariable(this.getProject(), name);
		this.getParent().getNode().replaceChild(this.getNode(), newVar.getNode());
		return newVar;
	}

	@Override
	public String getVarName() {
		return this.getName();
	}
}
