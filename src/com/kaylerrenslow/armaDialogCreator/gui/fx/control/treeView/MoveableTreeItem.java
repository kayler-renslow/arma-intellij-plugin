package com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import org.jetbrains.annotations.Nullable;

/**
 Created by Kayler on 05/15/2016.
 */
class MoveableTreeItem<E> extends TreeItem<TreeItemData<E>> {
	MoveableTreeItem(TreeItemData<E> data, @Nullable Node graphic) {
		super(data, graphic);
	}

	/**Creates a tree item that has a placeholder value*/
	MoveableTreeItem(){
		setValue(TreeItemData.getPlaceHolder());
		javafx.scene.control.Label lbl = new javafx.scene.control.Label("empty");
		lbl.setFont(Font.font(Font.getDefault().getFamily(), FontPosture.ITALIC, Font.getDefault().getSize()));
		setGraphic(lbl);
	}

	MoveableTreeItem(TreeItemData<E> data){
		this(data, null);
	}
}
