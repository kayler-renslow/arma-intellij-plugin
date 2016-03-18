package com.kaylerrenslow.a3plugin.lang.psiUtil;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;

import java.util.LinkedList;

/**
 * Created by Kayler on 01/02/2016.
 */
public class PsiUtil{

	public static boolean isOfElementType(ASTNode node, IElementType et){
		if(node == null){
			return false;
		}
		return node.getElementType().equals(IElementType.find(et.getIndex()));
	}

	public static boolean isOfElementType(PsiElement pe, IElementType et){
		if(pe == null){
			return false;
		}
		return isOfElementType(pe.getNode(), et);
	}

	public static LinkedList<PsiElement> findElements(PsiFile file, IElementType toFind, PsiElement cursor){
		LinkedList<PsiElement> list = new LinkedList<>();
		PsiElementFinder finder = new PsiElementFinder(list, toFind, cursor);
		traverseFile(file, finder);

		return list;
	}


	private static void traverseFile(PsiFile file, PsiElementDiscoveredEvent event){
		PsiElement[] children = file.getChildren();
		for(PsiElement p: children){
			traverseElement(file, p, event);
		}
	}

	private static void traverseElement(PsiFile file, PsiElement element, PsiElementDiscoveredEvent event){
		event.elementDiscovered(file, element);
		PsiElement[] children = element.getChildren();
		for(PsiElement p: children){
			traverseElement(file, p, event);
		}
	}


}
