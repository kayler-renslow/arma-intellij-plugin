package com.kaylerrenslow.armaDialogCreator.gui.fx.main;

import com.kaylerrenslow.armaDialogCreator.gui.fx.control.MenuUtil;
import com.kaylerrenslow.armaDialogCreator.gui.img.ImagePaths;
import com.kaylerrenslow.armaDialogCreator.main.Lang.MainMenuBar;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

/**
 Created by Kayler on 05/15/2016.
 */
class ADCMenuBar extends MenuBar implements EventHandler<ActionEvent> {
	private final CanvasView canvasView;

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
		SHOW_GRID(new CheckMenuItem(MainMenuBar.VIEW_SHOW_GRID)), COLORS(new Menu(MainMenuBar.VIEW_COLORS, null, new MenuItem(MainMenuBar.VIEW_COLORS_SELECTION, selectionColorPicker), new MenuItem(MainMenuBar.VIEW_COLORS_GRID, gridColorPicker))), CHANGE_BACKGROUND(new Menu(MainMenuBar.VIEW_CHANGE_BACKGROUND, null, ViewChangeBackground.IMAGE1.menuItem, ViewChangeBackground.IMAGE2.menuItem, ViewChangeBackground.IMAGE3.menuItem, ViewChangeBackground.COLOR.menuItem));

		private final MenuItem menuItem;

		View(MenuItem menuItem) {
			this.menuItem = menuItem;
		}
	}

	private static ColorPicker backgroundColorPicker = new ColorPicker(Color.WHITE);
	private static ColorPicker selectionColorPicker = new ColorPicker(Color.GREEN);
	private static ColorPicker gridColorPicker = new ColorPicker(Color.GRAY);

	private enum ViewChangeBackground {
		IMAGE1(new MenuItem(MainMenuBar.VIEW_CHANGE_BACKGROUND_IMAGE1)), IMAGE2(new MenuItem(MainMenuBar.VIEW_CHANGE_BACKGROUND_IMAGE2)), IMAGE3(new MenuItem(MainMenuBar.VIEW_CHANGE_BACKGROUND_IMAGE3)), COLOR(new MenuItem(MainMenuBar.VIEW_CHANGE_BACKGROUND_COLOR, backgroundColorPicker));
		private final MenuItem menuItem;

		ViewChangeBackground(MenuItem menuItem) {
			this.menuItem = menuItem;
		}
	}

	private final Menu menuFile = new Menu(MainMenuBar.FILE);
	private final Menu menuEdit = new Menu(MainMenuBar.EDIT);
	private final Menu menuView = new Menu(MainMenuBar.VIEW);


	ADCMenuBar(CanvasView canvasView) {
		this.canvasView = canvasView;
		initializeMenus();
		this.getMenus().addAll(menuFile, menuEdit, menuView);
		MenuUtil.addListeners(this, menuFile, menuEdit, menuView);
		MenuUtil.addListeners(this, (Menu) View.CHANGE_BACKGROUND.menuItem);
	}

	private void initializeMenus() {
		for (File f : File.values()) {
			menuFile.getItems().add(f.menuItem);
		}
		for (Edit e : Edit.values()) {
			menuEdit.getItems().add(e.menuItem);
		}
		for (View v : View.values()) {
			menuView.getItems().add(v.menuItem);
		}
		backgroundColorPicker.setMaxWidth(30);
		backgroundColorPicker.getStyleClass().add("button");
	}

	@Override
	public void handle(ActionEvent e) {
		Object source = e.getSource();
		for (View v : View.values()) {
			if (v == View.CHANGE_BACKGROUND) {
				for (ViewChangeBackground b : ViewChangeBackground.values()) {
					if (b.menuItem == source) {
						handle(b);
						return;
					}
				}
			}
			if (v.menuItem == source) {
				handle(v);
				return;
			}
		}
	}

	private void handle(ViewChangeBackground b) {
		if (b == ViewChangeBackground.IMAGE1) {
			canvasView.setCanvasBackgroundToImage(ImagePaths.BG_1);
		} else if (b == ViewChangeBackground.IMAGE2) {
			canvasView.setCanvasBackgroundToImage(ImagePaths.BG_2);
		} else if (b == ViewChangeBackground.IMAGE3) {
			canvasView.setCanvasBackgroundToImage(ImagePaths.BG_3);
		} else if (b == ViewChangeBackground.COLOR) {
			canvasView.setCanvasBackgroundToColor(backgroundColorPicker.getValue());
		}
	}

	private void handle(View v) {
		if (v == View.SHOW_GRID) {
			CheckMenuItem item = (CheckMenuItem) View.SHOW_GRID.menuItem;
			canvasView.showGrid(item.isSelected());
		} else if (v == View.COLORS) {
			canvasView.updateCanvasUIColors(gridColorPicker.getValue(), selectionColorPicker.getValue());
		}
	}

}
