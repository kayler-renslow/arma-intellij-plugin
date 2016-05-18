package com.kaylerrenslow.armaDialogCreator.gui.fx.control;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

/**
 Created by Kayler on 05/15/2016.
 */
public class MenuUtil {
	/**
	 Adds listeners to each of the menu items inside the given items.

	 @param handler handler that handles all actions for the given menus
	 @param menus menus to get items of and add action listeners
	 */
	public static void addListeners(EventHandler<ActionEvent> handler, @NotNull Menu... menus) {
		ObservableList<MenuItem> menuItems;
		for (Menu menu : menus) {
			menuItems = menu.getItems();
			for (MenuItem item : menuItems) {
				item.setOnAction(handler);
			}
		}
	}
}
