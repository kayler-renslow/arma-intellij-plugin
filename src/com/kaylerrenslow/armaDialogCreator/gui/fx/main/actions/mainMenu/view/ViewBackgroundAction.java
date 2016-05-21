package com.kaylerrenslow.armaDialogCreator.gui.fx.main.actions.mainMenu.view;

import com.kaylerrenslow.armaDialogCreator.gui.img.ImagePaths;
import com.kaylerrenslow.armaDialogCreator.main.ArmaDialogCreator;
import com.kaylerrenslow.armaDialogCreator.main.Lang;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.stage.FileChooser;

import java.io.File;

/**
 Created by Kayler on 05/20/2016.
 */
public class ViewBackgroundAction implements EventHandler<ActionEvent> {
	public static int NO_IMAGE = 0;
	public static int IMAGE_1 = 1;
	public static int IMAGE_2 = 2;
	public static int IMAGE_3 = 3;
	public static int IMAGE_CUSTOM = 4;

	private final int background;

	public ViewBackgroundAction(int background) {
		this.background = background;
	}

	@Override
	public void handle(ActionEvent event) {
		if (background == IMAGE_1) {
			ArmaDialogCreator.getCanvasView().setCanvasBackgroundToImage(ImagePaths.BG_1);
		} else if (background == IMAGE_2) {
			ArmaDialogCreator.getCanvasView().setCanvasBackgroundToImage(ImagePaths.BG_2);
		} else if (background == IMAGE_3) {
			ArmaDialogCreator.getCanvasView().setCanvasBackgroundToImage(ImagePaths.BG_3);
		} else if (background == NO_IMAGE) {
			ArmaDialogCreator.getCanvasView().setCanvasBackgroundToImage(null);
		} else if (background == IMAGE_CUSTOM) {
			FileChooser c = new FileChooser();
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Images", "*.png", "*.gif", "*.jpg", "*.mpo");
			c.getExtensionFilters().add(filter);
			c.setSelectedExtensionFilter(filter);
			c.setTitle(Lang.Misc.FILE_CHOOSER_BACKGROUND_IMG_TITLE);
			File chosen = c.showOpenDialog(ArmaDialogCreator.getPrimaryStage());
			if (chosen != null) {
				ArmaDialogCreator.getCanvasView().setCanvasBackgroundToImage(chosen.toURI().toString());
			}
		}
	}
}
