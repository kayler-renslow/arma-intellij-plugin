package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPrivateDeclVar;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFPrivateDeclVarReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Kayler on 03/23/2016.
 */
public class SQFPrivateDeclVarMixin extends ASTWrapperPsiElement implements SQFVariableBase{

	private TextRange range;
	private String varName;

	public SQFPrivateDeclVarMixin(@NotNull ASTNode node) {
		super(node);
		this.range = TextRange.from(node.getStartOffset() + 1, node.getTextLength() - 1);
		this.varName = super.getText().substring(1, this.getTextLength() - 1);
	}

	@Override
	public String getText() {
		return this.varName;
	}

	@Override
	public TextRange getTextRange() {
		return range;
	}

	@Override
	public ItemPresentation getPresentation() {
		String text = getText();
		String location = this.getContainingFile().getName();
		return new ItemPresentation(){
			@Nullable
			@Override
			public String getPresentableText() {
				return text;
			}

			@Nullable
			@Override
			public String getLocationString() {
				return location;
			}

			@Nullable
			@Override
			public Icon getIcon(boolean unused) {
				return PluginIcons.ICON_SQF_VARIABLE;
			}
		};
	}

	@Override
	public PsiReference getReference() {
		return new SQFPrivateDeclVarReference((SQFVariableNamedElement) getReferences()[0].getElement(), (SQFPrivateDeclVar) this);
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		ArrayList<ASTNode> nodes = PsiUtil.findDescendantElements(SQFPsiUtil.getCurrentScope(this), SQFTypes.VARIABLE, null, this.varName);
		PsiReference[] references = new PsiReference[nodes.size()];
		for (int i = 0; i < nodes.size(); i++){
			references[i] = nodes.get(i).getPsi().getReference();
		}
		return references;
	}

	@Override
	public String getVarName(){
		return this.varName;
	}
}
