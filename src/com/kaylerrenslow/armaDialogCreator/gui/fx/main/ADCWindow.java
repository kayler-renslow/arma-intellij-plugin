package com.kaylerrenslow.armaDialogCreator.gui.fx.main;

import com.kaylerrenslow.armaDialogCreator.gui.canvas.IPositionCalculator;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.Component;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.UICanvas;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.contextMenu.ComponentContextMenuCreator;
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
	private UICanvas canvas = new UICanvas(700, 700, new ComponentContextMenuCreator(), new IPositionCalculator() {

		@Override
		public double getGridScale() {
			return 1;
		}

		@Override
		public boolean snapEnabled() {
			return true;
		}

		@Override
		public int snapAmount() {
			return 20;
		}
	});

	public ADCWindow(Stage primaryStage) {
		this.primaryStage = primaryStage;
		Scene scene = new Scene(rootElement);
		this.primaryStage.setScene(scene);
		initialize();
		show();
	}

	private void initialize() {
		this.rootElement.getChildren().add(canvas);
		Color[] colors = {Color.RED, Color.BLACK, Color.ORANGE};
		int w = 60;
		int x = canvas.getPositionCalculator().snapAmount();
		for (Color c : colors) {
			Component component = new Component(x, 50, w, w);
			component.setBackgroundColor(c);
			component.setText(c.toString());
			if(c == colors[0]){
				component.setEnabled(false);
			}
			canvas.addComponent(component);
			x += canvas.getPositionCalculator().snapAmount();
		}
	}

	private void show() {
		this.primaryStage.show();
	}

}
