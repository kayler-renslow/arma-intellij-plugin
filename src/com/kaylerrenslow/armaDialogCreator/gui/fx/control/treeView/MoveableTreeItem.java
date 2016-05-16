package com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.Nullable;

/**
 Created by Kayler on 05/15/2016.
 */
public class MoveableTreeItem extends TreeItem<MoveableTreeNode> {
	public MoveableTreeItem(String text, @Nullable Node graphic) {
		super(new MoveableTreeNode(text), graphic);
	}

	/**Creates a tree item that has a placeholder value*/
	MoveableTreeItem(){
		setValue(MoveableTreeNode.getPlaceHolder());
	}

	public MoveableTreeItem(String text){
		this(text, null);
	}
}
