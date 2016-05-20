package com.kaylerrenslow.armaDialogCreator.gui.fx.main;

import com.kaylerrenslow.armaDialogCreator.gui.fx.control.MenuUtil;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.PopupColorPicker;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.PresetCheckMenuItem;
import com.kaylerrenslow.armaDialogCreator.gui.img.ImagePaths;
import com.kaylerrenslow.armaDialogCreator.main.Lang.MainMenuBar;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.Label;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

/**
 Created by Kayler on 05/15/2016.
 */
class ADCMenuBar extends MenuBar implements EventHandler<ActionEvent> {
	private final CanvasView canvasView;

	private static class File {
		static final MenuItem neww = new MenuItem(MainMenuBar.FILE_NEW);
		static final MenuItem open = new MenuItem(MainMenuBar.FILE_OPEN);
		static final MenuItem save = new MenuItem(MainMenuBar.FILE_SAVE);
		static final MenuItem saveAs = new MenuItem(MainMenuBar.FILE_SAVE_AS);
		static final MenuItem[] all = {neww, open, save, saveAs};
	}

	private static class Edit {
		static final MenuItem viewChanges = new MenuItem(MainMenuBar.EDIT_CHANGES);
		static final MenuItem undo = new MenuItem(MainMenuBar.EDIT_UNDO);
		static final MenuItem redo = new MenuItem(MainMenuBar.EDIT_REDO);
		static MenuItem[] all = {viewChanges, undo, redo};
	}

	private static class View {
		static final PresetCheckMenuItem showGrid = new PresetCheckMenuItem(MainMenuBar.VIEW_SHOW_GRID, true);

		static class AbsRegion {
			static final PresetCheckMenuItem show = new PresetCheckMenuItem(MainMenuBar.VIEW_ABS_REGION_SHOW, true);
			static final PresetCheckMenuItem alwaysFront = new PresetCheckMenuItem(MainMenuBar.VIEW_ABS_REGION_ALWAYS_FRONT, true);
			static final Menu all = new Menu(MainMenuBar.VIEW_ABS_REGION, null, show, alwaysFront);
		}

		static class Colors {
			static final ColorPicker selectionColorPicker = new ColorPicker(DefaultColors.UICanvasEditor.SELECTION);
			static final ColorPicker gridColorPicker = new ColorPicker(DefaultColors.UICanvasEditor.GRID);
			static final ColorPicker absRegionColorPicker = new ColorPicker(DefaultColors.UICanvasEditor.ABS_REGION);

			static final MenuItem absRegion = new MenuItem(MainMenuBar.VIEW_COLORS_ABS_REGION, absRegionColorPicker);
			static final MenuItem selection = new MenuItem(MainMenuBar.VIEW_COLORS_SELECTION, selectionColorPicker);
			static final MenuItem grid = new MenuItem(MainMenuBar.VIEW_COLORS_GRID, gridColorPicker);
			static final Menu all = new Menu(MainMenuBar.VIEW_COLORS, null, selection, grid, absRegion);
		}

		static class Background {
			static final ColorPicker backgroundColorPicker = new ColorPicker(DefaultColors.UICanvasEditor.EDITOR_BG);
			static final Button backgroundColorPickerBtn = new Button(MainMenuBar.VIEW_CHANGE_BACKGROUND_COLOR);

			static final MenuItem img1 = new MenuItem(null, Label.create(MainMenuBar.VIEW_CHANGE_BACKGROUND_IMAGE1, Color.BLACK));
			static final MenuItem img2 = new MenuItem(null, Label.create(MainMenuBar.VIEW_CHANGE_BACKGROUND_IMAGE2, Color.BLACK));
			static final MenuItem img3 = new MenuItem(null, Label.create(MainMenuBar.VIEW_CHANGE_BACKGROUND_IMAGE3, Color.BLACK));
			static final MenuItem color = new MenuItem("", backgroundColorPickerBtn);
			static final Menu all = new Menu(MainMenuBar.VIEW_CHANGE_BACKGROUND, null, img1, img2, img3, color);
		}

		static final MenuItem[] all = {showGrid, AbsRegion.all, Colors.all, Background.all};
	}

	private final Menu menuFile = new Menu(MainMenuBar.FILE, null, File.all);
	private final Menu menuEdit = new Menu(MainMenuBar.EDIT, null, Edit.all);
	private final Menu menuView = new Menu(MainMenuBar.VIEW, null, View.all);


	ADCMenuBar(CanvasView canvasView) {
		this.canvasView = canvasView;
		initializeColorPickerMenus();
		this.getMenus().addAll(menuFile, menuEdit, menuView);
		MenuUtil.addListeners(this, menuFile, menuEdit, menuView);

	}

	private void initializeColorPickerMenus() {
		View.Background.backgroundColorPickerBtn.setOnAction(new ColorPickerButtonEvent(canvasView, View.Background.backgroundColorPicker, View.Background.color));
	}

	@Override
	public void handle(ActionEvent e) {
		Object source = e.getSource();
		for (MenuItem m : menuFile.getItems()) {
			if (m == source) {
				handleFile(e.getTarget());
			}
		}
		for (MenuItem m : menuEdit.getItems()) {
			if (m == source) {
				handleEdit(e.getTarget());
			}
		}
		for (MenuItem m : menuView.getItems()) {
			if (m == source) {
				handleView(e.getTarget());
			}
		}
	}

	private void handleView(Object target) {
		if (target == View.showGrid) {
			canvasView.showGrid(View.showGrid.isSelected());
			return;
		}
		if (target == View.Background.img1) {
			canvasView.setCanvasBackgroundToImage(ImagePaths.BG_1);
			return;
		} else if (target == View.Background.img2) {
			canvasView.setCanvasBackgroundToImage(ImagePaths.BG_2);
			return;
		} else if (target == View.Background.img3) {
			canvasView.setCanvasBackgroundToImage(ImagePaths.BG_3);
			return;
		} else if (target == View.Background.color) {
			canvasView.setCanvasBackgroundToColor(View.Background.backgroundColorPicker.getValue());
			return;
		}
		if (target == View.Colors.grid || target == View.Colors.selection) {
			canvasView.updateCanvasUIColors(View.Colors.gridColorPicker.getValue(), View.Colors.selectionColorPicker.getValue());
			return;
		}
		if (target == View.Colors.absRegion) {
			canvasView.updateAbsRegionColor(View.Colors.absRegionColorPicker.getValue());
			return;
		}
		if (target == View.AbsRegion.alwaysFront || target == View.AbsRegion.show) {
			canvasView.updateAbsRegion(View.AbsRegion.alwaysFront.isSelected(), View.AbsRegion.show.isSelected());
		}

	}

	private void handleEdit(Object target) {

	}

	private void handleFile(Object target) {

	}

	private static class ColorPickerButtonEvent implements EventHandler<ActionEvent> {

		private final ColorPicker picker;
		private final CanvasView view;
		private final MenuItem menuItem;

		public ColorPickerButtonEvent(CanvasView view, ColorPicker picker, MenuItem menuItem) {
			this.picker = picker;
			this.view = view;
			this.menuItem = menuItem;
		}

		@Override
		public void handle(ActionEvent event) {
			PopupColorPicker popup = new PopupColorPicker(picker);
			Point2D p = view.getUiCanvasEditor().localToScreen(0, 0);
			popup.show(view.getUiCanvasEditor(), p.getX(), p.getY());
			popup.getPicker().setOnHidden(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					popup.hide();
					menuItem.fire();
				}
			});
		}
	}

}
