package com.kaylerrenslow.armaDialogCreator.gui.fx.control.treeView;

/**
 Created by Kayler on 05/15/2016.
 */
public interface ITreeNodeUpdate {
	void delete();

	void renamed(String parentName);

	void requestEdit();
}
