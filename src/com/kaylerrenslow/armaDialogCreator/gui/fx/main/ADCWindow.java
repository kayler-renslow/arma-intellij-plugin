package com.kaylerrenslow.armaDialogCreator.gui.fx.main;

import com.kaylerrenslow.armaDialogCreator.util.Constants;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;

/**
 Created by Kayler on 05/11/2016.
 */
public class ADCWindow {
	private final Stage primaryStage;
	private final VBox rootElement = new VBox();
	private final ADCMenuBar mainMenuBar = new ADCMenuBar();
	private Constants.CanvasDimension canvasDimension = Constants.CanvasDimension.D1366;
	private final CanvasView canvasView = new CanvasView(canvasDimension.width, canvasDimension.height);

	public ADCWindow(Stage primaryStage) {
		this.primaryStage = primaryStage;

		Scene scene = new Scene(rootElement);
		this.primaryStage.setScene(scene);
		initialize(scene);
		show();

	}

	private void initialize(Scene scene) {
		rootElement.getChildren().addAll(mainMenuBar, canvasView);
		rootElement.minWidth(canvasDimension.width + 250.0);
		rootElement.minHeight(canvasDimension.height + 50.0);
		EventHandler<KeyEvent> keyEvent = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				canvasView.keyEvent(event.getText(), event.getEventType() == KeyEvent.KEY_PRESSED, event.isShiftDown(), event.isControlDown(), event.isAltDown());
			}
		};
		scene.setOnKeyPressed(keyEvent);
		scene.setOnKeyReleased(keyEvent);
		scene.getMnemonics().clear();

	}

	private void show() {
		this.primaryStage.show();
	}

}
