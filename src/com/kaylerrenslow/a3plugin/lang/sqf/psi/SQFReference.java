package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.kaylerrenslow.a3plugin.lang.psiUtil.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by Kayler on 03/20/2016.
 */
public class SQFReference extends PsiReferenceBase<PsiElement> implements PsiReference/* implements PsiPolyVariantReference use this interface for command calling on files*/{

	private String variable;

	public SQFReference(PsiElement element, TextRange rangeInElement) {
		super(element, rangeInElement);
		variable = element.getText();
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		ArrayList<ASTNode> elements = PsiUtil.findElements(myElement.getContainingFile(), SQFTypes.VARIABLE, null);
		return elements.size() > 0 ? elements.get(0).getPsi() : null;
	}

	@NotNull
	@Override
	public Object[] getVariants() {
		ArrayList<LookupElement> variants = new ArrayList<>();
		ArrayList<ASTNode> elements = PsiUtil.findElements(myElement.getContainingFile(), SQFTypes.VARIABLE, null);
		for(ASTNode node : elements){
			variants.add(LookupElementBuilder.create(node.getPsi()));
		}
		return variants.toArray();
	}
}
