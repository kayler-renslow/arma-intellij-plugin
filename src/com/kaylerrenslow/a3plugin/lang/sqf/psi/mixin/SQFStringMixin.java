package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.util.ArrayUtil;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFLocalVarInStringReference;
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
		SQFScope searchScope = SQFPsiUtil.getContainingScope(this);
		SQFStatement myStatement = (SQFStatement) PsiUtil.getFirstAncestorOfType(this.getNode(), SQFTypes.STATEMENT, null).getPsi();
		if (myStatement.getExpression() instanceof SQFCommandExpression) { //check if String is a for loop variable (for "_var" from 0 to 10 do{})
			SQFCommandExpression commandExpression = (SQFCommandExpression) myStatement.getExpression();
			if (commandExpression.getCommandName().equals("for")) {
				if (commandExpression.getPostfixArgument() instanceof SQFCommandExpression) {
					SQFCommandExpression forPostfixExp = (SQFCommandExpression) commandExpression.getPostfixArgument();
					if (forPostfixExp.getPrefixArgument() instanceof SQFLiteralExpression) {
						SQFLiteralExpression possibleStringLiteral = (SQFLiteralExpression) forPostfixExp.getPrefixArgument();
						if (possibleStringLiteral != null && possibleStringLiteral.getString() == this) { //is a for loop variable
							//now set the search scope to the code block next to 'do'
							SQFCodeBlock doCodeBlock = SQFPsiUtil.getAPostfixArgument(forPostfixExp, SQFCodeBlock.class);
							if (doCodeBlock != null) {
								searchScope = doCodeBlock.getLocalScope();
							}
						}
					}
				}
			}
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
		if(refVars.size() > 0){
			return ArrayUtil.mergeArrays(referencesFromProviders, new PsiReference[]{new SQFLocalVarInStringReference(refVars, searchScope, this)});
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
