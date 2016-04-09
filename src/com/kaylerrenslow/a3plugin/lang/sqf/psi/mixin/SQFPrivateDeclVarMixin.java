package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiReference;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation.SQFPrivateDeclVarItemPresentation;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFLocalVarDeclReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Kayler
 *         PsiElement mixin for SQF grammar file. This mixin is meant for PrivateDeclVar PsiElements. (variables in strings next to private keyword)
 *         Created on 03/23/2016.
 */
public class SQFPrivateDeclVarMixin extends ASTWrapperPsiElement implements SQFPrivateDeclNamedElement {

	public SQFPrivateDeclVarMixin(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public ItemPresentation getPresentation() {
		return new SQFPrivateDeclVarItemPresentation((SQFPrivateDeclVar) this);
	}

	@Override
	public PsiReference getReference() {
		return getReferences()[0];
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		SQFScope myContainingScope = SQFPsiUtil.getContainingScope(this);
		ArrayList<ASTNode> nodes = PsiUtil.findDescendantElements(myContainingScope, SQFTypes.VARIABLE, null, this.getVarName());
		ArrayList<PsiReference> references = new ArrayList<>();
		SQFVariable var;
		for (ASTNode node : nodes) {
			var = (SQFVariable) node.getPsi();
			if (var.getDeclarationScope() == myContainingScope) {
				references.add(new SQFLocalVarDeclReference(var, (SQFPrivateDeclVar) this));
			}
		}
		return references.toArray(new PsiReference[references.size()]);
	}

	@Override
	public String getVarName() {
		return super.getText().substring(1, this.getTextLength() - 1); //do not store it because when the text is changed, the stored value wouldn't be up to date
	}

}
