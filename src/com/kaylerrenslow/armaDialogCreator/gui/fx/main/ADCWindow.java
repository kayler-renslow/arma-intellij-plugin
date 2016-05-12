package com.kaylerrenslow.armaDialogCreator.gui.fx.main;

import com.kaylerrenslow.armaDialogCreator.gui.lib.ui.UICanvas;
import com.kaylerrenslow.armaDialogCreator.gui.lib.ui.api.PaintedRegion;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created by Kayler on 05/11/2016.
 */
public class ADCWindow {
	private final Stage primaryStage;
	private final VBox rootElement = new VBox();
	private UICanvas canvas = new UICanvas(500, 500);

	public ADCWindow(Stage primaryStage) {
		this.primaryStage = primaryStage;
		Scene scene = new Scene(rootElement);
		this.primaryStage.setScene(scene);

		initialize();
		show();
	}

	private void initialize() {
		this.rootElement.getChildren().add(canvas.getCanvasElement());
		Color[] colors = {Color.RED, Color.BLACK, Color.ORANGE};
		int x = 30;
		int w = 30;
		for (Color c : colors) {
			PaintedRegion region = new PaintedRegion(x, 50, w, w);
			region.setPaint(c);
			canvas.addRegion(region);
			x += w + 5;
		}
	}

	private void show() {
		this.primaryStage.show();
	}

}
