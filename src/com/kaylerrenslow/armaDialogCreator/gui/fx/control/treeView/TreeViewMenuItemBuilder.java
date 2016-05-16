package com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

/**
 Created by Kayler on 05/15/2016.
 */
public class TreeViewMenuItemBuilder {

	public static void setNewFolderAction(EditableTreeView treeView, MenuItem menuItem, String defaultFolderName){
		menuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TreeItem<MoveableTreeNode> selected = treeView.getSelectedItem();
				treeView.addFolderToParent(selected.getParent(), defaultFolderName);
			}
		});
	}
}
