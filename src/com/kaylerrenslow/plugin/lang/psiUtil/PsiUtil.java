package com.kaylerrenslow.plugin.lang.psiUtil;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import java.util.LinkedList;

/**
 * Created by Kayler on 01/02/2016.
 */
public class PsiUtil{

	public static boolean isOfElementType(PsiElement pe, IElementType et){
		if(pe == null){
			return false;
		}
		if(pe.getNode() == null){
			return false;
		}
		return pe.getNode().getElementType().equals(IElementType.find(et.getIndex()));
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
