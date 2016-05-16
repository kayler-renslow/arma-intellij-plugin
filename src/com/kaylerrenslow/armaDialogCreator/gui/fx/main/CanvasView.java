package com.kaylerrenslow.armaDialogCreator.gui.fx.main;

import com.kaylerrenslow.armaDialogCreator.gui.canvas.UICanvas;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.Component;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

/**
 Created by Kayler on 05/15/2016.
 */
class CanvasView extends HBox {
	private final UICanvas uiCanvas;
	private final CanvasControls canvasControls = new CanvasControls();

	CanvasView(int canvasWidth, int canvasHeight) {
		this.uiCanvas = new UICanvas(canvasWidth, canvasHeight, canvasControls);
//		this.uiCanvas.setMenuCreator(new ComponentContextMenuCreator());
		this.uiCanvas.setCanvasContextMenu(new ContextMenu(new MenuItem("Canvas Context Menu")));

		this.getChildren().addAll(uiCanvas, canvasControls);

		HBox.setHgrow(canvasControls, Priority.ALWAYS);

		setOnMouseMoved(new CanvasViewMouseEvent(this));

		addRandomThings();
		focusToCanvas(true);
	}

	private void focusToCanvas(boolean focusToCanvas) {
		canvasControls.setFocusTraversable(!focusToCanvas);
		uiCanvas.setFocusTraversable(focusToCanvas);
		if (focusToCanvas) {
			uiCanvas.requestFocus();
		}
	}

	private void addRandomThings() {
		Color[] colors = {Color.RED, Color.BLACK, Color.ORANGE, Color.PURPLE};
		int w = 100;
		int x = uiCanvas.getPositionCalculator().smallestSnapPercentage() * 100;
		for (Color c : colors) {
			Component component = new Component(x, 50, w, w);
			component.setBackgroundColor(c);
			component.setText(c.toString());
			if (c == colors[0]) {
				component.setEnabled(false);
			}
			if (c == colors[2]) {
				//				component.setGhost(true);
			}
			uiCanvas.addComponent(component);
			x += uiCanvas.getPositionCalculator().smallestSnapPercentage() * 30;
		}
	}


	void setCanvasSize(int width, int height) {
		this.uiCanvas.setCanvasSize(width, height);
	}


	void keyEvent(String text, boolean keyDown, boolean shiftDown, boolean controlDown, boolean altDown) {
		uiCanvas.keyEvent(text, keyDown, shiftDown, controlDown, altDown);
	}

	private static class CanvasViewMouseEvent implements EventHandler<MouseEvent> {

		private final CanvasView canvasView;

		CanvasViewMouseEvent(CanvasView canvasView) {
			this.canvasView = canvasView;
		}

		@Override
		public void handle(MouseEvent event) {
			canvasView.focusToCanvas(event.getTarget() == canvasView.uiCanvas.getCanvas());
		}
	}
}
