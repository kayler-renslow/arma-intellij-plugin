package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.util.ArrayUtil;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFLocalVarsInStringReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 @author Kayler
 PsiElement mixin for SQF grammar file. This mixin is meant for PrivateDeclVar PsiElements. (variables in strings next to private keyword)
 Created on 03/23/2016. */
public abstract class SQFStringMixin extends ASTWrapperPsiElement implements SQFString {

	public SQFStringMixin(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public PsiReference getReference() {
		PsiReference[] references = getReferences();
		if (references.length == 0) {
			return null;
		}
		return references[0];
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		PsiReference[] referencesFromProviders = ReferenceProvidersRegistry.getReferencesFromProviders(this);
		if (!stringContainsLocalVar()) {
			return referencesFromProviders;
		}
		SQFScope searchScope;
		SQFScope forScope = SQFPsiUtil.getForVarScope(this);
		if (forScope != null) {
			searchScope = forScope;
		} else {
			searchScope = SQFPsiUtil.getFileScope((SQFFile) getContainingFile());
		}

		ArrayList<ASTNode> nodes = PsiUtil.findDescendantElements(searchScope, SQFTypes.VARIABLE, null, this.getNonQuoteText());
		SQFVariable var;
		String myVarName = this.getNonQuoteText();
		ArrayList<SQFVariable> refVars = new ArrayList<>(nodes.size());
		for (ASTNode node : nodes) {
			var = (SQFVariable) node.getPsi();

			if (var.getVarName().equals(myVarName) && var.getDeclarationScope() == searchScope) {
				refVars.add(var);
			}
		}
		if (refVars.size() > 0) {
			return ArrayUtil.mergeArrays(referencesFromProviders, new PsiReference[]{new SQFLocalVarsInStringReference(refVars, searchScope, this)});
		}
		return referencesFromProviders;
	}

	private boolean stringContainsLocalVar() {
		String nonquote = getNonQuoteText();
		if (nonquote.length() == 0) {
			return false;
		}
		return nonquote.charAt(0) == '_' && !nonquote.contains(" ");
	}

	public String getNonQuoteText() {
		return getText().substring(1, getTextLength() - 1);
	}

	@Override
	public String toString() {
		return "SQFStringMixin{" + getText() + "}";
	}
}
