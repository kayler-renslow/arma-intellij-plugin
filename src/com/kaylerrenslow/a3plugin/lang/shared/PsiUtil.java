package com.kaylerrenslow.a3plugin.lang.shared;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.Queue;
import com.kaylerrenslow.a3plugin.util.TraversalObjectFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 * @since 01/02/2016
 */
public class PsiUtil {

	public static ASTNode getFirstDescendantNode(PsiElement element) {
		ASTNode cursor = element.getNode();
		while (cursor.getFirstChildNode() != null) {
			cursor = cursor.getFirstChildNode();
		}
		return cursor;
	}

	/**
	 * Traverses the entire ast tree with BFS, starting from start. Each node that is found will be sent to finder. It is also possible to stop the traversal at any time with finder
	 *
	 * @param start  starting ASTNode
	 * @param finder TraversalObjectFinder
	 */
	public static void traverseBreadthFirstSearch(@NotNull ASTNode start, @NotNull TraversalObjectFinder<ASTNode> finder) {
		finder.found(start);
		ASTNode[] children = start.getChildren(null);
		Queue<ASTNode> nodes = new Queue<>(children.length);

		for (ASTNode child : children) {
			nodes.addLast(child);
		}
		ASTNode node;
		while (nodes.size() > 0) {
			node = nodes.pullFirst();
			finder.found(node);
			if (finder.stopped()) {
				return;
			}
			if (finder.traverseFoundNodesChildren()) {
				children = node.getChildren(null);
				for (ASTNode child : children) {
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
		return getFirstAncestorOfType(node, type, textContent) != null;
	}

	/**
	 * Checks if the given node has an ancestor of the given IElementType. If there is one, this method will return that ancestor. Otherwise, it will return null.<br>
	 * If textContent is not null, this method will also check if the ancestor is of correct type and ancestor's text is equal to textContent.
	 *
	 * @param node        node to check if has a parent of IElementType type
	 * @param type        IElementType to check
	 * @param textContent null if to disregard text of ancestor, otherwise check if ancestor's text is equal to textContent
	 * @return node's ancestor if ancestor is of IElementType type if node's ancestor's text matches textContent. If textContent is null, text can be anything for ancestor.
	 */
	@Nullable
	public static ASTNode getFirstAncestorOfType(@NotNull ASTNode node, @NotNull IElementType type, @Nullable String textContent) {
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
	 * Checks if the given node has an ancestor that inherits from the given class. If there is one, this method will return that ancestor. Otherwise, it will return null.<br>
	 * If textContent is not null, this method will also check if the ancestor is of correct type and ancestor's text is equal to textContent.
	 *
	 * @param start       where to start traversing upwards
	 * @param clazz       type to check
	 * @param textContent null if to disregard text of ancestor, otherwise check if ancestor's text is equal to textContent
	 * @return node's ancestor if ancestor is of IElementType type if node's ancestor's text matches textContent. If textContent is null, text can be anything for ancestor.
	 */
	@Nullable
	public static <T extends PsiElement> T getFirstAncestorOfType(@NotNull PsiElement start, @NotNull Class<T> clazz, @Nullable String textContent) {
		PsiElement parent = start.getParent();
		boolean isChild = false;
		while (parent != null && !isChild) {
			parent = parent.getParent();
			if (parent == null) {
				break;
			}
			isChild = clazz.isInstance(parent) && (textContent == null || parent.getText().equals(textContent));
		}
		if (isChild) {
			return (T) parent;
		}
		return null;
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
	public static boolean isOfElementType(@Nullable ASTNode node, @NotNull IElementType et) {
		return node != null && node.getElementType() == et;
	}

	/**
	 * Checks if the given PsiElement is of IElementType et
	 *
	 * @param pe PsiElement (if null, returns false)
	 * @param et IElement type
	 * @return true if pe is of type et, false otherwise
	 */
	public static boolean isOfElementType(@Nullable PsiElement pe, @NotNull IElementType et) {
		return pe != null && isOfElementType(pe.getNode(), et);
	}


	@Nullable
	public static <T extends PsiElement> T findFirstDescendantElement(@NotNull PsiElement element, @NotNull Class<T> type) {
		PsiElement child = element.getFirstChild();
		while (child != null) {
			if (type.isInstance(child)) {
				return (T) child;
			}
			T e = findFirstDescendantElement(child, type);
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
			if (astNode != null) {
				return astNode;
			}
		}
		return null;
	}

	public static <E extends PsiElement> ArrayList<E> findDescendantElementsOfInstance(@NotNull PsiElement rootElement, @NotNull Class<E> type, @Nullable PsiElement cursor, @Nullable String textContent) {
		ArrayList<E> list = new ArrayList<>();
		findDescdantElementsOfInstance(rootElement, type, cursor, textContent, list);
		return list;
	}

	public static <E extends PsiElement> ArrayList<E> findDescendantElementsOfInstance(@NotNull PsiElement rootElement, @NotNull Class<E> type, @Nullable PsiElement cursor) {
		return findDescendantElementsOfInstance(rootElement, type, cursor, null);
	}

	private static <E extends PsiElement> void findDescdantElementsOfInstance(PsiElement rootElement, Class<?> type, PsiElement cursor, String textContent, ArrayList<E> list) {
		PsiElement child = rootElement.getFirstChild();
		while (child != null) {
			if (cursor != null && child == cursor) {
				continue;
			}
			if (type.isAssignableFrom(child.getClass()) && (textContent == null || child.getText().equals(textContent))) {
				list.add((E) child);
			}
			findDescdantElementsOfInstance(child, type, cursor, textContent, list);
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


	/**
	 * Get children of the given PsiElement that extend/are type of the given class
	 *
	 * @param element  element to get children of
	 * @param psiClass class
	 * @return list of all children
	 */
	@NotNull
	public static <T extends PsiElement> List<T> findChildrenOfType(PsiElement element, Class<T> psiClass) {
		List<T> list = new ArrayList<T>();
		PsiElement[] children = element.getChildren();
		for (PsiElement child : children) {
			if (psiClass.isInstance(child)) {
				list.add((T) child);
			}
		}
		return list;
	}
}
