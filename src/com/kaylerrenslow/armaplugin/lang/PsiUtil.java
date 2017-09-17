package com.kaylerrenslow.armaplugin.lang;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 09/06/2017
 */
public class PsiUtil {

	/**
	 * Creates and returns a PsiFile with the given text of the given file type.
	 *
	 * @throws ClassCastException when the PsiFile created couldn't be cast to T
	 */
	@NotNull
	public static <T extends PsiFile> T createFile(@NotNull Project project, @NotNull String text, @NotNull FileType fileType) {
		String fileName = "fake_sqf_file.sqf";
		return (T) PsiFileFactory.getInstance(project).createFileFromText(fileName, fileType, text);
	}

	@NotNull
	public static ASTNode getFirstDescendantNode(@NotNull PsiElement element) {
		ASTNode cursor = element.getNode();
		while (cursor.getFirstChildNode() != null) {
			cursor = cursor.getFirstChildNode();
		}
		return cursor;
	}

	/**
	 * Will traverse up the AST tree and send each discovered node into the callback, including the starting node.
	 * The function can return true, which will then terminate the traversal, or return false or null to continue the traversal.
	 *
	 * @param start    where to start
	 * @param callback function invoked for each discovered node
	 */
	public static void traverseUp(@NotNull ASTNode start, @NotNull Function<ASTNode, Boolean> callback) {
		Boolean stop = callback.apply(start);
		if (stop != null && stop) {
			return;
		}
		ASTNode parent = start;
		while (parent != null) {
			parent = parent.getTreeParent();
			stop = callback.apply(parent);
			if (stop != null && stop) {
				return;
			}
		}
	}

	/**
	 * Traverses the entire ast tree with BFS, starting from start. Each node that is found will be sent to callback.
	 * It is also possible to stop the traversal at any time with callback by returning true in it
	 *
	 * @param start    starting ASTNode
	 * @param callback TraversalObjectFinder
	 */
	public static void traverseBreadthFirstSearch(@NotNull ASTNode start, @NotNull Function<ASTNode, Boolean> callback) {
		Boolean stop = callback.apply(start);
		if (stop != null && stop) {
			return;
		}
		ASTNode[] children = start.getChildren(null);
		LinkedList<ASTNode> nodes = new LinkedList<>();

		for (ASTNode child : children) {
			nodes.addLast(child);
		}
		ASTNode node;
		while (nodes.size() > 0) {
			node = nodes.removeFirst();
			stop = callback.apply(node);
			if (stop != null && stop) {
				return;
			}
			children = node.getChildren(null);
			for (ASTNode child : children) {
				nodes.addLast(child);
			}
		}
	}

	/**
	 * Traverses the entire ast tree with BFS, starting from start. Each node that is found will be sent to callback.
	 * It is also possible to stop the traversal at any time with callback by returning true in it. When returning true,
	 * the traversal will prevent the current node's children from being traversed.
	 * <p>
	 * This method will not terminate until all discovered nodes are traversed. If the callback returns true, there may
	 * still be nodes left to traverse because of the current node's parent had multiple children.
	 *
	 * @param start    starting ASTNode
	 * @param callback TraversalObjectFinder
	 */
	public static void traverseInLayers(@NotNull ASTNode start, @NotNull Function<ASTNode, Boolean> callback) {
		Boolean stop = callback.apply(start);
		if (stop != null && stop) {
			return;
		}
		ASTNode[] children = start.getChildren(null);
		LinkedList<ASTNode> nodes = new LinkedList<>();

		for (ASTNode child : children) {
			nodes.addLast(child);
		}
		ASTNode node;
		while (nodes.size() > 0) {
			node = nodes.removeFirst();
			stop = callback.apply(node);
			if (stop != null && stop) {
				continue;
			}
			children = node.getChildren(null);
			for (ASTNode child : children) {
				nodes.addLast(child);
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


	@NotNull
	public static <E extends PsiElement> List<E> findDescendantElementsOfInstance(@NotNull PsiElement rootElement,
																				  @NotNull Class<E> type,
																				  @Nullable PsiElement cursor,
																				  @Nullable String textContent) {
		ArrayList<E> list = new ArrayList<>();
		findDescendantElementsOfInstance(rootElement, type, cursor, textContent, list);
		return list;
	}

	private static <E extends PsiElement> void findDescendantElementsOfInstance(@NotNull PsiElement rootElement,
																				@NotNull Class<?> type,
																				@Nullable PsiElement cursor,
																				@Nullable String textContent,
																				@NotNull List<E> list) {
		PsiElement child = rootElement.getFirstChild();
		while (child != null) {
			if (cursor != null && child == cursor) {
				continue;
			}
			if (type.isAssignableFrom(child.getClass()) && (textContent == null || child.getText().equals(textContent))) {
				list.add((E) child);
			}
			findDescendantElementsOfInstance(child, type, cursor, textContent, list);
			child = child.getNextSibling();
		}
	}


	/**
	 * Get children of the given PsiElement that extend/are type of the given class
	 *
	 * @param element  element to get children of
	 * @param psiClass class
	 * @return list of all children
	 */
	@NotNull
	public static <T extends PsiElement> List<T> findChildrenOfType(@NotNull PsiElement element, @NotNull Class<T> psiClass) {
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
