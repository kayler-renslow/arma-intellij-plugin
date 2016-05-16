package com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;

/**
 Created by Kayler on 05/15/2016.
 */
public class TreeViewMenuItemBuilder {

	public static<E> void setNewFolderAction(@NotNull EditableTreeView treeView, @NotNull MenuItem menuItem, @NotNull String defaultFolderName, @NotNull E data) {
		menuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TreeItem<TreeItemData> selected = treeView.getSelectedItem();
				TreeItemData<E> treeItemData = new TreeItemData<E>(defaultFolderName, CellType.FOLDER, data);
				if (selected != null) {
					if(selected.getValue().isFolder()){
						treeView.addFolderToParent(selected, treeItemData);
					}else{
						treeView.addFolderToParent(selected.getParent(), treeItemData);
					}
				} else {
					treeView.addChildToRoot(treeView.getSelectedIndex(), treeItemData);
				}
			}
		});
	}
}
