package com.kaylerrenslow.a3plugin.lang.shared;

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
	 * Checks if IElementType of both nodes are the same. Returns false if either are null.
	 *
	 * @param node1 ASTNode
	 * @param node2 ASTNode
	 * @return true if node1's element type is node2's element type, false otherwise
	 */
	public static boolean isSameElementType(ASTNode node1, ASTNode node2) {
		return node1 != null && node2 != null && node1.getElementType() == node1.getElementType();
	}

	/**
	 * Checks if IElementType of both elements are the same. Returns false if either are null.
	 *
	 * @param psiElement1 PsiElement
	 * @param psiElement2 PsiElement
	 * @return true if psiElement's element type is node2's element type, false otherwise
	 */
	public static boolean isSameElementType(PsiElement psiElement1, PsiElement psiElement2) {
		return isSameElementType(psiElement1.getNode(), psiElement2.getNode());
	}

	/**
	 * Checks if the given ASTNode is of IElementType et
	 *
	 * @param node ASTNode (if null, returns false)
	 * @param et   IElement type
	 * @return true if node is of type et, false otherwise
	 */
	public static boolean isOfElementType(ASTNode node, IElementType et) {
		return node != null && node.getElementType() == et;
	}

	/**
	 * Checks if the given PsiElement is of IElementType et
	 *
	 * @param pe PsiElement (if null, returns false)
	 * @param et IElement type
	 * @return true if pe is of type et, false otherwise
	 */
	public static boolean isOfElementType(PsiElement pe, IElementType et) {
		return pe != null && isOfElementType(pe.getNode(), et);
	}

	/**
	 * Traverses the entire AST tree of the given PsiFile and returns the first ASTNode that matches IElementType type
	 *
	 * @param file    PsiFile to traverse
	 * @param type    IElement the type to find in the AST tree
	 * @param content text to match inside the node, or null if doesn't matter
	 * @return ASTNode that is the first of type, or null if none was found
	 */
	public static ASTNode findFirstElement(PsiFile file, IElementType type, String content) {
		ASTNode[] children = file.getNode().getChildren(null);
		ASTNode ret;
		for (ASTNode child : children){
			ret = findFirstElement(child, type, content);
			if (ret != null){
				return ret;
			}
		}
		return null;
	}

	/**
	 * Traverses the entire AST tree of the given ASTNode and returns the first ASTNode that matches IElementType type
	 *
	 * @param node    ASTNode to traverse
	 * @param type    IElement the type to find in the AST tree
	 * @param content text to match inside the node, or null if doesn't matter
	 * @return ASTNode that is the first of type, or null if none was found
	 */
	public static ASTNode findFirstElement(ASTNode node, IElementType type, String content) {
		if (isOfElementType(node, type)){
			if (content != null && node.getText().equals(content)){
				return node;
			}else if (content != null){
				return null;
			}
			return node;
		}
		ASTNode[] children = node.getChildren(null);
		for (ASTNode child : children){
			findFirstElement(child, type, content);
		}
		return null;
	}

	/**
	 * Traverses the entire AST tree of the given PsiFile and adds all ASTNodes that match the type of toFind to a list
	 *
	 * @param file   PsiFile to traverse
	 * @param toFind IElement the type to find in the AST tree
	 * @param cursor the node that is already discovered since the user's mouse is over it (can be null)
	 * @return ArrayList containing all ASTNodes that mach the IElementType toFind
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
