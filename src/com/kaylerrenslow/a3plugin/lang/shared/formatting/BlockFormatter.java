package com.kaylerrenslow.a3plugin.lang.shared.formatting;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * Created by Kayler on 03/27/2016.
 */
public abstract class BlockFormatter{
	public Wrap childWrap;
	public Indent childIndent;
	public Alignment childAlignment;
	public boolean allowGrandChildrenToIndent;

	public void format(PsiElement currentElement, ASTNode childNode, int childNum, boolean allowGrandChildrenToIndent){
		childIndent = null;
		childAlignment = null;
		childWrap = null;
		formatNode(currentElement, childNode, childNum, allowGrandChildrenToIndent);
	}

	protected abstract void formatNode(PsiElement currentElement, ASTNode childNode, int childNum, boolean allowChildrenToIndent);
}
