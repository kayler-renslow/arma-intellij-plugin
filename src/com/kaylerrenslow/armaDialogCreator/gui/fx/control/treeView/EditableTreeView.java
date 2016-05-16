package com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Creates a new EditableTreeView with a root node already in place. This class extends javafx.scene.control.TreeView of type TreeItemData */
public class EditableTreeView<E> extends javafx.scene.control.TreeView<TreeItemData<E>> {
	private final String folderIconURL;

	public EditableTreeView(@Nullable ITreeCellSelectionUpdate selectionUpdate, @Nullable String url) {
		super(new MoveableTreeItem());
		this.showRootProperty().set(false);

		this.setEditable(true);
		this.folderIconURL = url;
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		setCellSelectionUpdate(selectionUpdate);
	}

	public void setCellSelectionUpdate(@Nullable ITreeCellSelectionUpdate selectionUpdate) {
		setCellFactory(new TreeFactoryGen<>(new EditableTreeCellFactory(selectionUpdate)));
	}


	/**
	 Adds a new TreeItem to the tree.

	 @param data data inside tree node
	 */
	public void addChildToRoot(TreeItemData<E> data) {
		TreeItem<TreeItemData<E>> item;
		if (data.getCellType() == CellType.FOLDER) {
			item = createFolder(data);
		} else {
			item = new MoveableTreeItem<E>(data);
		}

		addChildToRoot(item);
	}

	/**
	 Adds a new TreeItem to the tree.

	 @param index where to add the child at
	 @param data data inside node
	 */
	public void addChildToRoot(int index, TreeItemData<E> data) {
		if (index < 0) {
			addChildToRoot(data);
			return;
		}
		getRoot().getChildren().add(index, new MoveableTreeItem<>(data));
	}


	public int getSelectedIndex() {
		return getSelectionModel().getSelectedIndex();
	}

	public TreeItemData<E> getItem(int index) {
		return getRoot().getChildren().get(index).getValue();
	}


	/**
	 Removes a child at the given index of the given root

	 @param i index of the child
	 */
	void removeChild(@NotNull TreeItem<TreeItemData<E>> parent, int i) {
		removeChild(parent, parent.getChildren().get(i));
	}

	/**
	 Removes the specified child from the tree of the given parent.

	 @param parent what TreeItem the item is a child of
	 @param toRemove item to remove
	 */
	void removeChild(@NotNull TreeItem<TreeItemData<E>> parent, @NotNull TreeItem<TreeItemData<E>> toRemove) {
		for (TreeItem<TreeItemData<E>> item : parent.getChildren()) {
			TreeUtil.<E>stepThroughChildren(item, new IFoundChild() {
				@Override
				public <E> void found(TreeItem<TreeItemData<E>> found) {
					found.getValue().delete();
				}
			});

		}
		toRemove.getValue().delete();
		parent.getChildren().remove(toRemove);
		// if the parent is a folder, add a placeholder item in it if the folder is not empty
		if (parent.getValue().isFolder() && parent.getChildren().size() == 0) {
			parent.getChildren().add(new TreeItem<>());
		}
	}


	/**
	 Adds a child to a designated parent.

	 @param parent parent node
	 @param child node to be made the child of parent
	 */
	void addChildToParent(@NotNull TreeItem<TreeItemData<E>> parent, @NotNull TreeItem<TreeItemData<E>> child) {
		// if the parent is a folder, remove the placeholder item in that folder if there is one
		if (parent.getValue().isFolder() && parent.getChildren().size() == 1 && parent.getChildren().get(0).getValue().isPlaceholder()) {
			parent.getChildren().remove(0);
		}
		parent.getChildren().add(child);
	}


	/**
	 Adds a child to the root

	 @param item item to be added
	 */
	void addChildToRoot(@NotNull TreeItem<TreeItemData<E>> item) {
		getRoot().getChildren().add(item);
	}

	void addFolderToParent(@NotNull TreeItem<TreeItemData<E>> parent, @NotNull TreeItemData<E> data) {
		TreeItem<TreeItemData<E>> folder = createFolder(data);
		addChildToParent(parent, folder);
	}


	@Nullable
	TreeItem<TreeItemData<E>> getSelectedItem() {
		return getSelectionModel().getSelectedItem();
	}

	private TreeItem<TreeItemData<E>> createFolder(@NotNull TreeItemData<E> data) {
		MoveableTreeItem<E> folder = new MoveableTreeItem<E>(data, getFolderIcon());
		folder.getChildren().add(new MoveableTreeItem());
		return folder;
	}

	private ImageView getFolderIcon() {
		return this.folderIconURL != null ? new ImageView(this.folderIconURL) : null;
	}

}
