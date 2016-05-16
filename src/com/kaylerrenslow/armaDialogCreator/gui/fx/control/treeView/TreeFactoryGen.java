package com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView;

import javafx.scene.control.TreeCell;
import javafx.util.Callback;

class TreeFactoryGen implements Callback<javafx.scene.control.TreeView<MoveableTreeNode>, TreeCell<MoveableTreeNode>> {
	private EditableTreeCellFactory factory;

	public TreeFactoryGen(EditableTreeCellFactory factory) {
		this.factory = factory;
	}

	@Override
	public TreeCell<MoveableTreeNode> call(javafx.scene.control.TreeView<MoveableTreeNode> view) {
		return this.factory.getNewInstance();
	}
}
