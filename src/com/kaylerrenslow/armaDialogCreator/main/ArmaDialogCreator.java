package com.kaylerrenslow.armaDialogCreator.main;

import com.kaylerrenslow.armaDialogCreator.gui.fx.main.ADCWindow;
import com.kaylerrenslow.armaDialogCreator.gui.fx.main.ICanvasView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 @author Kayler
 Created on 05/11/2016.
 */
public class ArmaDialogCreator extends Application {
	private static Stage primaryStage;
	private static ADCWindow mainWindow;

	public static void main(String[] args) {
		ArmaDialogCreator.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainWindow = new ADCWindow(primaryStage);
		ArmaDialogCreator.primaryStage = primaryStage;
		primaryStage.setTitle(Lang.Application.APPLICATION_TITLE);
	}

	public static ICanvasView getCanvasView() {
		return mainWindow.getCanvasView();
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}
}
