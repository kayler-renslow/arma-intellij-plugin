package com.kaylerrenslow.armaDialogCreator.main;

import com.kaylerrenslow.armaDialogCreator.gui.fx.main.ADCWindow;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Kayler on 05/11/2016.
 */
public class ArmaDialogCreator extends Application{
	public static void main(String[] args){
		ArmaDialogCreator.launch(args);
	}

	private ADCWindow mainWindow;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.mainWindow = new ADCWindow(primaryStage);
	}
}
