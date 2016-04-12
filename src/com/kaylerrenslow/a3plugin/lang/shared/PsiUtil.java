package com.kaylerrenslow.a3plugin.lang.shared;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.containers.Queue;
import com.kaylerrenslow.a3plugin.util.TraversalObjectFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 *         Created on 01/02/2016.
 */
public class PsiUtil {

	/**Traverses the entire ast tree with BFS, starting from start. Each node that is found will be sent to finder. It is also possible to stop the traversal at any time with finder
	 * @param start starting ASTNode
	 * @param finder TraversalObjectFinder
	 */
	public static void traverseBreadthFirstSearch(@NotNull ASTNode start, @NotNull TraversalObjectFinder<ASTNode> finder){
		finder.found(start);
		ASTNode[] children = start.getChildren(null);
		Queue<ASTNode> nodes = new Queue<>(children.length);

		for(ASTNode child : children){
			nodes.addLast(child);
		}
		ASTNode node;
		while(nodes.size() > 0){
			node = nodes.pullFirst();
			finder.found(node);
			if(finder.stopped()){
				return;
			}
			if(finder.traverseFoundNodesChildren()){
				children = node.getChildren(null);
				for(ASTNode child : children){
					nodes.addLast(child);
				}
			}
		}
	}

	/**
	 * Gets the closest next sibling, that is non-whitespace, relative to node
	 *
	 * @param node node to find sibling of
	 * @return non-whitespace sibling, or null if none was found
	 */
	@Nullable
	public static ASTNode getNextSiblingNotWhitespace(@NotNull ASTNode node) {
		return getNextSiblingNotType(node, TokenType.WHITE_SPACE);
	}

	/**
	 * Gets the closest next sibling, where the type is not skip, relative to node
	 *
	 * @param node node to find sibling of
	 * @param skip the token to skip
	 * @return non-skip sibling, or null if none was found
	 */
	@Nullable
	public static ASTNode getNextSiblingNotType(@NotNull ASTNode node, @NotNull IElementType skip) {
		ASTNode sibling = node.getTreeNext();
		while (sibling != null) {
			if (sibling.getElementType() == skip) {
				sibling = sibling.getTreeNext();
			} else {
				break;
			}
		}
		return sibling;
	}

	/**
	 * Gets the closest previous sibling, that is non-whitespace, relative to node
	 *
	 * @param node node to find sibling of
	 * @return non-whitespace sibling, or null if none was found
	 */
	@Nullable
	public static ASTNode getPrevSiblingNotWhitespace(@NotNull ASTNode node) {
		return getPrevSiblingNotType(node, TokenType.WHITE_SPACE);
	}

	/**
	 * Gets the closest previous sibling, that is not skip, relative to node
	 *
	 * @param node node to find sibling of
	 * @param skip what element type to skip
	 * @return non-whitespace sibling, or null if none was found
	 */
	@Nullable
	public static ASTNode getPrevSiblingNotType(@NotNull ASTNode node, @NotNull IElementType skip) {
		ASTNode sibling = node.getTreePrev();
		while (sibling != null) {
			if (sibling.getElementType() == skip) {
				sibling = sibling.getTreePrev();
			} else {
				break;
			}
		}
		return sibling;
	}

	/**
	 * Checks if the given node is a descendant of the given IElementType.<br>
	 * If textContent is not null, this method will also check if the ancestor is of correct type and ancestor's text is equal to textContent.
	 *
	 * @param node        node to check if has a ancestor of IElementType type
	 * @param type        IElementType to check
	 * @param textContent null if to disregard text of ancestor, otherwise check if ancestor's text is equal to textContent
	 * @return true if node has ancestor of IElementType type and ancestor's text matches textContent. If textContent is null, text can be anything for ancestor.
	 */
	public static boolean isDescendantOf(@NotNull ASTNode node, @NotNull IElementType type, @Nullable String textContent) {
		return getAncestorWithType(node, type, textContent) != null;
	}

	/**
	 * Checks if the given node is an ancestor of the given IElementType. If it is, this method will return that ancestor. Otherwise, it will return null.<br>
	 * If textContent is not null, this method will also check if the ancestor is of correct type and ancestor's text is equal to textContent.
	 *
	 * @param node        node to check if has a parent of IElementType type
	 * @param type        IElementType to check
	 * @param textContent null if to disregard text of ancestor, otherwise check if ancestor's text is equal to textContent
	 * @return node's ancestor if ancestor is of IElementType type if node's ancestor's text matches textContent. If textContent is null, text can be anything for ancestor.
	 */
	@Nullable
	public static ASTNode getAncestorWithType(@NotNull ASTNode node, @NotNull IElementType type, @Nullable String textContent) {
		ASTNode parent = node.getTreeParent();
		boolean isChild = false;
		while (parent != null && !isChild) {
			parent = parent.getTreeParent();
			if (parent == null) {
				break;
			}
			isChild = parent.getElementType() == type && (textContent == null || parent.getText().equals(textContent));
		}
		return parent;
	}

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
	public static boolean isOfElementType(@NotNull PsiElement pe, @NotNull IElementType et) {
		return pe != null && isOfElementType(pe.getNode(), et);
	}


	@Nullable
	public static PsiElement findFirstDescendantElement(@NotNull PsiElement element, @NotNull Class<?> type) {
		PsiElement child = element.getFirstChild();
		while (child != null) {
			if (type.isInstance(child)) {
				return child;
			}
			PsiElement e = findFirstDescendantElement(child, type);
			if (e != null) {
				return e;
			}
			child = child.getNextSibling();
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
	@Nullable
	public static ASTNode findFirstDescendantElement(@NotNull ASTNode node, @NotNull IElementType type, @Nullable String content) {
		if (isOfElementType(node, type)) {
			if (content != null && node.getText().equals(content)) {
				return node;
			} else if (content != null) {
				return null;
			}
			return node;
		}
		ASTNode[] children = node.getChildren(null);
		ASTNode astNode;
		for (ASTNode child : children) {
			astNode = findFirstDescendantElement(child, type, content);
			if(astNode != null){
				return astNode;
			}
		}
		return null;
	}


	public static <E extends PsiElement> ArrayList<E> findDescendantElementsOfInstance(PsiElement rootElement, Class<?> type, PsiElement cursor) {
		ArrayList<E> list = new ArrayList<>();
		findDescdantElementsOfInstance(rootElement, type, cursor, list);
		return list;
	}

	private static <E extends PsiElement> void findDescdantElementsOfInstance(PsiElement rootElement, Class<?> type, PsiElement cursor, ArrayList<E> list) {
		PsiElement child = rootElement.getFirstChild();
		while (child != null) {
			if (cursor != null && child == cursor) {
				continue;
			}
			if (type.isAssignableFrom(child.getClass())) {
				list.add((E) child);
			}
			findDescdantElementsOfInstance(child, type, cursor, list);
			child = child.getNextSibling();
		}
	}

	/**
	 * Traverses the entire AST tree of the given PsiElement and adds all ASTNodes that match the type of toFind to a list
	 *
	 * @param element PsiElement to traverse
	 * @param toFind  IElement the type to find in the AST tree
	 * @param cursor  the node that is already discovered since the user's mouse is over it (can be null)
	 * @return ArrayList containing all ASTNodes that mach the IElementType toFind
	 */
	public static ArrayList<ASTNode> findDescendantElements(PsiElement element, IElementType toFind, ASTNode cursor) {
		return findDescendantElements(element, toFind, cursor, null);
	}

	/**
	 * Traverses the entire AST tree of the given PsiElement and adds all ASTNodes that match the type of toFind to a list and the ASTNode's text equals textContent
	 *
	 * @param element     PsiElement to traverse
	 * @param toFind      IElement the type to find in the AST tree
	 * @param cursor      the node that is already discovered since the user's mouse is over it (can be null)
	 * @param textContent text to look for in ASTNode (null if doesn't matter)
	 * @return ArrayList containing all ASTNodes that mach the IElementType toFind
	 */
	public static ArrayList<ASTNode> findDescendantElements(PsiElement element, IElementType toFind, ASTNode cursor, @Nullable String textContent) {
		ArrayList<ASTNode> list = new ArrayList<>();
		traverseElement(list, element, toFind, cursor, textContent);
		return list;
	}

	/**
	 * Traverses the entire AST tree of the given PsiElement and adds all ASTNodes that match the type of toFind to a list
	 *
	 * @param list        list to add each ASTNode to that matches toFind's type
	 * @param element     PsiElement to traverse
	 * @param toFind      IElement the type to find in the AST tree
	 * @param cursor      the node that is already discovered since the user's mouse is over it (can be null)
	 * @param textContent text to look for in ASTNode (null if doesn't matter)
	 */
	private static void traverseElement(ArrayList<ASTNode> list, PsiElement element, IElementType toFind, ASTNode cursor, String textContent) {
		ASTNode[] children = element.getNode().getChildren(null);
		for (ASTNode node : children) {
			traverseASTNode(list, toFind, cursor, node, textContent);
		}
	}

	/**
	 * Traverses all children of discoveredElement and adds ASTNode's that mach type of toFind to list
	 *
	 * @param list              list of all elements of type toFind
	 * @param toFind            element type to find
	 * @param cursor            the ASTNode that has the mouse over it (can be null)
	 * @param discoveredElement previously discovered ASTNode
	 * @param textContent       text to look for in ASTNode (null if doesn't matter)
	 */
	private static void traverseASTNode(ArrayList<ASTNode> list, IElementType toFind, ASTNode cursor, ASTNode discoveredElement, String textContent) {
		if (textContent == null) {
			elementDiscovered(list, toFind, cursor, discoveredElement);
		} else if (discoveredElement.getText().equals(textContent)) {
			elementDiscovered(list, toFind, cursor, discoveredElement);
		}
		ASTNode[] children = discoveredElement.getChildren(null);
		for (ASTNode node : children) {
			traverseASTNode(list, toFind, cursor, node, textContent);
		}
	}

	private static void elementDiscovered(ArrayList<ASTNode> elements, IElementType toFind, ASTNode cursor, ASTNode discoveredElement) {
		if (PsiUtil.isOfElementType(discoveredElement, toFind)) {
			addToList(elements, discoveredElement, cursor);
		}
	}

	private static void addToList(List<ASTNode> list, ASTNode n, ASTNode cursor) {
		if (n == null) {
			return;
		}
		if (n == cursor) {
			return;
		}
		list.add(n);
	}

}
