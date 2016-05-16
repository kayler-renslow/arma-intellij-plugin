package com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView;

import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Created by Kayler on 05/15/2016.
 */
public class TreeItemFolder extends MoveableTreeItem {
	public TreeItemFolder(@NotNull String folderName, @Nullable ImageView folderIcon) {
		super(folderName, folderIcon);
		getChildren().add(new MoveableTreeItem());
		setGraphic(folderIcon);

		getValue().setIsFolder(true);
	}
}
