package com.kaylerrenslow.a3plugin.lang.psiUtil;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 *         Created on 01/02/2016.
 */
public class PsiUtil{

	/**
	 * Checks if the given ASTNode is of IElementType et
	 *
	 * @param node ASTNode (if null, returns false)
	 * @param et   IElement type
	 * @return true if node is of type et, false otherwise
	 */
	public static boolean isOfElementType(ASTNode node, IElementType et) {
		if (node == null){
			return false;
		}
		return node.getElementType().equals(IElementType.find(et.getIndex()));
	}

	/**
	 * Checks if the given PsiElement is of IElementType et
	 *
	 * @param pe PsiElement (if null, returns false)
	 * @param et IElement type
	 * @return true if pe is of type et, false otherwise
	 */
	public static boolean isOfElementType(PsiElement pe, IElementType et) {
		if (pe == null){
			return false;
		}
		return isOfElementType(pe.getNode(), et);
	}

	/**
	 * Traverses the entire AST tree of the given PsiFile and adds all ASTNodes that match the type of toFind to a list
	 *
	 * @param file   PsiFile to traverse
	 * @param toFind IElement the type to find in the AST tree
	 * @param cursor the node that is already discovered since the user's mouse is over it (can be null)
	 * @returns ArrayList containing all ASTNodes that mach the IElementType toFind
	 */
	public static ArrayList<ASTNode> findElements(PsiFile file, IElementType toFind, ASTNode cursor) {
		ArrayList<ASTNode> list = new ArrayList<>();
		traverseFile(list, file, toFind, cursor);

		return list;
	}

	/**
	 * Traverses the entire AST tree of the given PsiFile and adds all ASTNodes that match the type of toFind to a list
	 *
	 * @param list   list to add each ASTNode to that matches toFind's type
	 * @param file   PsiFile to traverse
	 * @param toFind IElement the type to find in the AST tree
	 * @param cursor the node that is already discovered since the user's mouse is over it (can be null)
	 */
	private static void traverseFile(ArrayList<ASTNode> list, PsiFile file, IElementType toFind, ASTNode cursor) {
		ASTNode[] children = file.getNode().getChildren(null);
		for (ASTNode node : children){
			traverseASTNode(list, toFind, cursor, node);
		}
	}

	/**
	 * Traverses all children of discoveredElement and adds ASTNode's that mach type of toFind to list
	 *
	 * @param list              list of all elements of type toFind
	 * @param toFind            element type to find
	 * @param cursor            the ASTNode that has the mouse over it (can be null)
	 * @param discoveredElement previously discovered ASTNode
	 */
	private static void traverseASTNode(ArrayList<ASTNode> list, IElementType toFind, ASTNode cursor, ASTNode discoveredElement) {
		elementDiscovered(list, toFind, cursor, discoveredElement);
		ASTNode[] children = discoveredElement.getChildren(null);
		for (ASTNode node : children){
			traverseASTNode(list, toFind, cursor, node);
		}
	}

	private static void elementDiscovered(ArrayList<ASTNode> elements, IElementType toFind, ASTNode cursor, ASTNode discoveredElement) {
		if (PsiUtil.isOfElementType(discoveredElement, toFind)){
			addToList(elements, discoveredElement, cursor);
		}
	}

	private static void addToList(List<ASTNode> list, ASTNode n, ASTNode cursor) {
		if (n == null){
			return;
		}
		if (n == cursor){
			return;
		}
		list.add(n);
	}

}
