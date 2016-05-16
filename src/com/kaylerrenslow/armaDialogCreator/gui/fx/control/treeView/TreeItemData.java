package com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TreeItemData<E> {

	private static int lastId = 0;

	private final long ID = lastId++;

	private final E data;
	private final CellType cellType;
	private final ITreeNodeUpdateListener updateListener;

	private final boolean isPlaceholder;

	private String text;

	/**
	 Creates a place holder TreeItemData to be used in folders

	 @return a new place holder TreeItemData
	 */
	static TreeItemData getPlaceHolder() {
		return new TreeItemData();
	}

	private TreeItemData(){
		this.isPlaceholder = true;
		data = null;
		cellType = null;
		updateListener = null;
	}

	public TreeItemData(@NotNull String text, @NotNull CellType cellType, @NotNull E data, @Nullable ITreeNodeUpdateListener updateListener) {
		this.isPlaceholder = false;
		this.text = text;
		this.cellType = cellType;
		this.data = data;
		this.updateListener = updateListener;
	}


	public TreeItemData(@NotNull String text, @NotNull CellType cellType, @NotNull E data) {
		this(text, cellType, data, null);
	}

	public ITreeNodeUpdateListener getUpdateListener() {
		return updateListener;
	}

	public E getData() {
		return data;
	}

	public final CellType getCellType() {
		return cellType;
	}

	public final boolean isFolder() {
		return cellType == CellType.FOLDER;
	}

	boolean isPlaceholder() {
		return isPlaceholder;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof TreeItemData) {
			return this.ID == ((TreeItemData) other).ID;
		}
		return false;
	}

	@Override
	public final String toString() {
		return text;
	}

	public final String getText() {
		return text;
	}

	void setText(String t) {
		this.text = t;
		if (this.updateListener != null) {
			this.updateListener.renamed(t);
		}
	}

	void delete() {
		if (this.updateListener != null) {
			this.updateListener.delete();
		}
	}

}
