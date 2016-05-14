package com.kaylerrenslow.armaDialogCreator.gui.canvas;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.awt.*;

/**
 * Created by Kayler on 05/13/2016.
 */
class CanvasMouseEvent implements EventHandler<MouseEvent> {
	private final UICanvas canvas;

	CanvasMouseEvent(UICanvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void handle(MouseEvent event) {
		MouseButton btn = event.getButton();
		if (!(event.getTarget() instanceof Canvas)) {
			return;
		}
		canvas.mouseEvent(event.isShiftDown(), event.isControlDown(), event.isAltDown());
		Point2D contextMenuPosition = canvas.getContextMenuPosition();
		ContextMenu cm = canvas.getContextMenu();

		if (cm != null) {
			if (cm.isShowing()) {
				double x = contextMenuPosition.getX();
				double y = contextMenuPosition.getY();
				int distance = (int) Point.distance(x, y, event.getX(), event.getY());
				if (distance > 10) {
					cm.hide();
				}
			}
		}
		Canvas c = (Canvas) event.getTarget();
		Point2D p = c.sceneToLocal(event.getSceneX(), event.getSceneY());
		int mousex = (int) p.getX();
		int mousey = (int) p.getY();

		if (event.getEventType() == MouseEvent.MOUSE_MOVED || event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			canvas.mouseMoved(mousex, mousey);
			canvas.setLastMousePosition(mousex, mousey);
		} else {
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
				canvas.mousePressed(mousex, mousey, btn);
			} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				canvas.mouseReleased(mousex, mousey, btn);
			}
		}
	}

}

