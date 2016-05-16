package com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView;


public class MoveableTreeNode {

	private static int lastId = 0;

	private String text;
	private final long ID = lastId++;
	private ITreeNodeUpdate userObject;
	private boolean allowedDifferentRoot = true;
	private boolean isFolder;

	private boolean isPlaceholder = false;

	/**
	 Creates a place holder MoveableTreeNode to be used in folders

	 @return a new place holder MoveableTreeNode
	 */
	static MoveableTreeNode getPlaceHolder() {
		MoveableTreeNode place = new MoveableTreeNode("", null);
		place.isPlaceholder = true;
		return place;
	}

	public MoveableTreeNode(String text) {
		this(text, null);
	}

	public MoveableTreeNode(String text, ITreeNodeUpdate userObject) {
		this.text = text;
		setUserObject(userObject);
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setIsFolder(boolean val) {
		this.isFolder = val;
	}

	public boolean isPlaceholder() {
		return isPlaceholder;
	}

	public void setUserObject(ITreeNodeUpdate obj) {
		this.userObject = obj;
	}

	public ITreeNodeUpdate getUserObject() {
		return userObject;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof MoveableTreeNode) {
			return this.ID == ((MoveableTreeNode) other).ID;
		}
		return false;
	}

	public boolean isAllowedDifferentRoot() {
		return allowedDifferentRoot;
	}

	public void setAllowedDifferentRoot(boolean value) {
		allowedDifferentRoot = value;
	}

	public MoveableTreeNode() {
		text = "";
	}

	@Override
	public String toString() {
		return text;
	}

	public void setText(String t) {
		this.text = t;
		if (userObject != null) {
			userObject.renamed(t);
		}
	}

	public String getText() {
		return text;
	}

	public void delete() {
		if (userObject != null) {
			userObject.delete();
		}
		System.out.println(text + " is being deleted " + (isPlaceholder ? "and it is a placeholder TreeNode" : "") + getClass());
	}

}
