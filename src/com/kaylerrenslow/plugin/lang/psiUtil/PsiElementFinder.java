package com.kaylerrenslow.plugin.lang.psiUtil;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;

import java.util.LinkedList;

/**
 * Created by Kayler on 01/02/2016.
 */
class PsiElementFinder extends PsiElementDiscoveredEvent{

	private LinkedList<PsiElement> elements;
	private IElementType toFind;
	private PsiElement cursor;

	public PsiElementFinder(LinkedList<PsiElement> elements, IElementType toFind, PsiElement cursor) {
		this.elements = elements;
		this.toFind = toFind;
		this.cursor = cursor;
	}

	@Override
	public void elementDiscovered(PsiFile file, PsiElement element) {
		addToList(this.elements, element.getNode().findChildByType(toFind));
	}

	private void addToList(LinkedList<PsiElement> list, ASTNode n){
		if(n == null){
			return;
		}
		if(n.getPsi() == this.cursor){
			return;
		}
		list.add(n.getPsi());
	}
}
