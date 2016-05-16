package com.kaylerrenslow.armaDialogCreator.gui.fx.main;

import com.kaylerrenslow.armaDialogCreator.gui.fx.action.IActionEventHandler;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.MenuUtil;
import com.kaylerrenslow.armaDialogCreator.main.Lang.MainMenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 Created by Kayler on 05/15/2016.
 */
class ADCMenuBar extends MenuBar implements IActionEventHandler {
	private enum File {
		NEW(new MenuItem(MainMenuBar.FILE_NEW)), OPEN(new MenuItem(MainMenuBar.FILE_OPEN)), SAVE(new MenuItem(MainMenuBar.FILE_SAVE)), SAVE_AS(new MenuItem(MainMenuBar.FILE_SAVE_AS));

		private final MenuItem menuItem;

		File(MenuItem menuItem) {
			this.menuItem = menuItem;
		}
	}

	private enum Edit {
		VIEW_CHANGES(new MenuItem(MainMenuBar.EDIT_CHANGES)), UNDO(new MenuItem(MainMenuBar.EDIT_UNDO)), REDO(new MenuItem(MainMenuBar.EDIT_REDO));

		private final MenuItem menuItem;

		Edit(MenuItem menuItem) {
			this.menuItem = menuItem;
		}
	}

	private enum View {
		TOGGLE_CANVAS_CONTROLS(new MenuItem(MainMenuBar.VIEW_TOGGLE_CANVAS_CONTROLS));

		private final MenuItem menuItem;

		View(MenuItem menuItem) {
			this.menuItem = menuItem;
		}
	}

	private final Menu menuFile = new Menu(MainMenuBar.FILE);
	private final Menu menuEdit = new Menu(MainMenuBar.EDIT);
	private final Menu menuView = new Menu(MainMenuBar.VIEW);


	ADCMenuBar() {
		initializeMenus();
		this.getMenus().addAll(menuFile, menuEdit, menuView);
		MenuUtil.addListeners(this, menuFile, menuEdit, menuView);
	}

	private void initializeMenus() {
		for (File f : File.values()) {
			menuFile.getItems().add(f.menuItem);
		}
		for(Edit e : Edit.values()){
			menuEdit.getItems().add(e.menuItem);
		}
		for(View v : View.values()){
			menuView.getItems().add(v.menuItem);
		}
	}

	@Override
	public void actionPerformed(Object source) {
		for (File f : File.values()) {
			if (f.menuItem == source) {
				handle(f);
				return;
			}
		}
		for (Edit e: Edit.values()) {
			if (e.menuItem == source) {
				handle(e);
				return;
			}
		}

	}

	private void handle(Edit e) {
		if(e == Edit.UNDO){
			e.menuItem.setText(String.format(MainMenuBar.EDIT_UNDO_F, "Something"));
			System.out.println("View Changes");
		}
	}

	private void handle(File f) {
		if (f == File.NEW) {
			System.out.println("New File");
		}
	}
}
