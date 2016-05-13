package com.kaylerrenslow.armaDialogCreator.gui.fx.control.contextMenu;

import com.kaylerrenslow.armaDialogCreator.gui.canvas.IComponentContextMenuCreator;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.Component;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Kayler on 05/12/2016.
 */
public class ComponentContextMenuCreator implements IComponentContextMenuCreator{

	@NotNull
	@Override
	public ContextMenu initialize(Component component) {
		ContextMenu cm = new ContextMenu(new MenuItem(component.getText()));
		return cm;
	}
}
