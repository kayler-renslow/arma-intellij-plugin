package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFPrivateDeclVarReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

/**
 * @author Kayler
 *         PsiElement mixin for SQF grammar file. This mixin is meant for PrivateDeclVar PsiElements. (variables in strings next to private keyword)
 *         Created on 03/23/2016.
 */
public class SQFPrivateDeclVarMixin extends ASTWrapperPsiElement implements SQFPrivateDeclNamedElement {

	private TextRange range;
	private String varName;

	public SQFPrivateDeclVarMixin(@NotNull ASTNode node) {
		super(node);
		this.range = TextRange.from(node.getStartOffset() + 1, node.getTextLength() - 1);
		this.varName = super.getText().substring(1, this.getTextLength() - 1);
//		System.out.println(this.getContainingFile().getName());
//		System.out.println("SQFPrivateDeclVarMixin.SQFPrivateDeclVarMixin " + this.varName + " " + super.getText());
	}

	@Override
	public TextRange getTextRange() {
		return range;
	}

	@Override
	public ItemPresentation getPresentation() {
		String text = getText();
		String location = this.getContainingFile().getName();
		return new ItemPresentation() {
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
		SQFScope myContainingScope = SQFPsiUtil.getContainingScope(this);
		ArrayList<ASTNode> nodes = PsiUtil.findDescendantElements(myContainingScope, SQFTypes.VARIABLE, null, this.varName);
		ArrayList<PsiReference> references = new ArrayList<>();
		SQFVariable var;
		for (ASTNode node : nodes) {
			var = (SQFVariable) node.getPsi();
			if (var.getDeclarationScope() == myContainingScope) {
				references.add(var.getReference());
			}
		}
		return references.toArray(new PsiReference[references.size()]);
	}

	@Override
	public String getVarName() {
		return this.varName;
	}

}
