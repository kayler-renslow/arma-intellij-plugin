package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFScope;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFLocalVarInStringReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Kayler
 *         PsiElement mixin for SQF grammar file. This mixin is meant for PrivateDeclVar PsiElements. (variables in strings next to private keyword)
 *         Created on 03/23/2016.
 */
public class SQFStringMixin extends ASTWrapperPsiElement {

	public SQFStringMixin(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public PsiReference getReference() {
		if(!stringContainsLocalVar()){
			return null;
		}
		return getReferences()[0];
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		if(!stringContainsLocalVar()){
			return new PsiReference[0];
		}
		SQFScope myContainingScope = SQFPsiUtil.getContainingScope(this);
		ArrayList<ASTNode> nodes = PsiUtil.findDescendantElements(myContainingScope, SQFTypes.VARIABLE, null, this.getNonQuoteText());
		ArrayList<PsiReference> references = new ArrayList<>();
		SQFVariable var;
		String myVarName = this.getNonQuoteText();
		for (ASTNode node : nodes) {
			var = (SQFVariable) node.getPsi();

			if (var.getVarName().equals(myVarName)) {
				references.add(new SQFLocalVarInStringReference(var, this));
			}
		}
		return references.toArray(new PsiReference[references.size()]);
	}

	private boolean stringContainsLocalVar(){
		if(this.getNonQuoteText().length() == 0){
			return false;
		}
		return this.getNonQuoteText().charAt(0) == '_';
	}

	public String getNonQuoteText() {
		return getText().substring(1, getTextLength() - 1);
	}
}
