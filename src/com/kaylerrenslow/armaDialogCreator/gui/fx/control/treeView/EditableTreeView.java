package com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView;

import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/** Creates a new EditableTreeView with a root node already in place. This class extends javafx.scene.control.TreeView of type MoveableTreeNode */
public class EditableTreeView extends javafx.scene.control.TreeView<MoveableTreeNode> {
	private final ArrayList<TreeItem<MoveableTreeNode>> roots = new ArrayList<>();

	public EditableTreeView(@NotNull String title, @Nullable ITreeCellSelectionUpdate selectionUpdate) {
		super(new MoveableTreeItem());
		this.showRootProperty().set(false);
		roots.add(new MoveableTreeItem(title));
		this.getRoot().getChildren().add(roots.get(0));

		this.setEditable(true);
		setCellSelectionUpdate(selectionUpdate);
	}

	public void setCellSelectionUpdate(ITreeCellSelectionUpdate selectionUpdate) {
		setCellFactory(new TreeFactoryGen(new EditableTreeCellFactory(selectionUpdate)));
	}


	/**
	 Gets the TreeItem at the given index and root number.

	 @param index index to retrieve the TreeItem from
	 @param rootNum root id
	 @return item at the given index of the given tree at root
	 */
	public TreeItem<MoveableTreeNode> getItem(int index, int rootNum) {
		checkRootNum(rootNum);
		return roots.get(rootNum).getChildren().get(index);
	}

	/**
	 Removes a child at the given index of the given root

	 @param i index of the child
	 */
	public void removeChild(TreeItem<MoveableTreeNode> parent, int i) {
		removeChild(parent, parent.getChildren().get(i));
	}

	/**
	 Removes the specified child from the tree of the given parent.

	 @param parent what TreeItem the item is a child of
	 @param toRemove item to remove
	 */
	public void removeChild(TreeItem<MoveableTreeNode> parent, TreeItem<MoveableTreeNode> toRemove) {
		for (TreeItem<MoveableTreeNode> item : parent.getChildren()) {
			TreeUtil.stepThroughChildren(item, new IFoundChild() {
				@Override
				public void found(TreeItem<MoveableTreeNode> found) {
					found.getValue().delete();
				}
			});

		}
		toRemove.getValue().delete();
		parent.getChildren().remove(toRemove);
		// if the parent is a folder, add a placeholder item in it if the folder is not empty
		if (parent.getValue().isFolder() && parent.getChildren().size() == 0) {
			parent.getChildren().add(new TreeItem<MoveableTreeNode>());
		}
	}

	/**
	 Gets the root number of the given root. Returns -1 if the given root it isn't actually a root.

	 @param root TreeItem to get the root number of
	 @return root number
	 */
	public int getRootNum(TreeItem<MoveableTreeNode> root) {
		int i = 0;
		for (TreeItem<MoveableTreeNode> item : this.roots) {
			if (root.equals(item)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 Returns the root at rootIndex

	 @param rootNum what the root index is
	 @return a root
	 */
	public TreeItem<MoveableTreeNode> getRoot(int rootNum) {
		checkRootNum(rootNum);
		return this.roots.get(rootNum);
	}

	/**
	 Adds a child to a designated parent.

	 @param parent parent node
	 @param child node to be made the child of parent
	 @param index index to add the child
	 */
	public void addChildToParent(TreeItem<MoveableTreeNode> parent, TreeItem<MoveableTreeNode> child, int index) {
		// if the parent is a folder, remove the placeholder item in that folder if there is one
		if (parent.getValue().isFolder() && parent.getChildren().size() == 1 && parent.getChildren().get(0).getValue().isPlaceholder()) {
			parent.getChildren().remove(0);
		}
		parent.getChildren().add(index, child);
	}


	public void addFolderToParent(TreeItem<MoveableTreeNode> parent, String folderName) {
		TreeItem<MoveableTreeNode> folder = new TreeItemFolder(folderName, null);
		folder.getValue().setAllowedDifferentRoot(true);
		addChildToParent(parent, folder, getSelectedIndex());
	}

	/**
	 Adds a child to the root at rootNum

	 @param item item to be added
	 @param rootNum root index
	 */
	public void addChildToRoot(TreeItem<MoveableTreeNode> item, int rootNum) {
		checkRootNum(rootNum);
		int size = this.roots.get(rootNum).getChildren().size();
		this.roots.get(rootNum).getChildren().add(size, item);
	}

	/**
	 Adds a new TreeItem to the the selected root of this tree. The root is selected by param rootNum..

	 @param child TreeItem text
	 @param rootNum root to select. The 0th root is the initial first root of the tree.
	 */
	public void addChildToRoot(String child, int rootNum) {
		checkRootNum(rootNum);
		TreeItem<MoveableTreeNode> item = new MoveableTreeItem(child);
		addChildToRoot(item, rootNum);
	}

	/**
	 Appends a new root to the tree and returns the root number (index at which the root was added. This can be used with addChildToRoot(TreeItem item, int rootNum))

	 @param rootName new root to add
	 @param expanded true if the root will be expanded when added, false otherwise
	 @return index where the root was added.
	 */
	public int appendNewRoot(String rootName, boolean expanded) {
		TreeItem<MoveableTreeNode> root = new MoveableTreeItem(rootName);
		this.roots.add(root);
		this.roots.get(roots.size() - 1).setExpanded(expanded);
		this.getRoot().getChildren().add(getRoot().getChildren().size(), root);
		return roots.size() - 1;
	}

	/**
	 Removes a root at the corresponding index, rootNum

	 @param rootNum root to remove
	 @throws IllegalArgumentException, IndexOutOfBoundsException
	 */
	public void removeRoot(int rootNum) {
		checkRootNum(rootNum);
		this.roots.remove(rootNum);
		this.getRoot().getChildren().remove(rootNum);
	}

	private int getSelectedIndex() {
		return getSelectionModel().getSelectedIndex();
	}

	private void checkRootNum(int rootNum) {
		if (rootNum < 0 || rootNum >= roots.size()) {
			throw new IndexOutOfBoundsException("No root exists at index " + rootNum + ".");
		}
	}

	public TreeItem<MoveableTreeNode> getSelectedItem() {
		return getSelectionModel().getSelectedItem();
	}
}
