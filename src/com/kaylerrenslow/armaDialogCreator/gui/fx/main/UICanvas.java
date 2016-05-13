package com.kaylerrenslow.armaDialogCreator.gui.fx.main;

import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.Component;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.PaintedRegion;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.Region;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.contextMenu.CanvasContextMenu;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;

/**
 * Created by Kayler on 05/11/2016.
 */
public class UICanvas extends Control {

	private final Canvas canvas;
	private final GraphicsContext gc;

	private Paint background = Color.WHITE;
	private Color selectionColor = Color.GREEN;
	private ArrayList<Component> components = new ArrayList<>();

	private Selection selection = new Selection();

	private MouseButton mouseButtonDown = null;
	private int lastMouseX, lastMouseY;
	private Component clickedComponent;

	public UICanvas(int width, int height) {
		this.canvas = new Canvas(width, height);
		this.getChildren().add(this.canvas);

		this.gc = this.canvas.getGraphicsContext2D();

		initializeListeners();
		paint();
	}

	private void initializeListeners() {

		setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
			@Override
			public void handle(ContextMenuEvent event) {
//				mousePressed((int) event.getX(), (int) event.getY(), MouseButton.SECONDARY);
//				mouseReleased((int) event.getX(), (int) event.getY(), MouseButton.SECONDARY);
//				setContextMenu(null);
//				if (clickedComponent != null) {
//					setContextMenu(new CanvasContextMenu(clickedComponent.getText()));
//				}
			}
		});
		this.canvas.setOnMousePressed(new CanvasMouseEvent(this));
		this.canvas.setOnMouseReleased(new CanvasMouseEvent(this));
		this.canvas.setOnMouseMoved(new CanvasMouseEvent(this));
		this.canvas.setOnMouseDragged(new CanvasMouseEvent(this));
	}

	/**
	 * Adds a component to the render
	 */
	public void addComponent(Component component) {
		this.components.add(component);
		paint();
	}

	/**
	 * Removes the given component from the render
	 *
	 * @param component component to add
	 * @return true if the component was removed, false if nothing was removed
	 */
	public boolean removeComponent(Component component) {
		boolean removed = this.components.remove(component);
		if (removed) {
			paint();
		}
		return removed;
	}

	private void paint() {
		gc.setFill(background);
		gc.fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
		for (PaintedRegion paintedRegion : components) {
			boolean select = false;
			if (paintedRegion == clickedComponent) {
				select = true;
			} else {
				for (PaintedRegion selected : selection.getSelectedRegions()) {
					if (selected == paintedRegion) {
						select = true;
						break;
					}
				}
			}
			paintRegion(paintedRegion, select);
		}

		if (selection.isSelecting()) {
			gc.save();
			gc.setStroke(selectionColor);
			selection.drawRectangle(gc);
			gc.restore();
		}
	}

	private void paintRegion(PaintedRegion region, boolean select) {
		gc.save();
		if (select) {
			gc.setEffect(new javafx.scene.effect.DropShadow(region.getWidth() / 2, Color.RED));
		}
		region.paint(gc);
		gc.restore();
	}

	private void mousePressed(int mousex, int mousey, MouseButton mb) {
		selection.setSelecting(false);
		this.mouseButtonDown = mb;
		clickedComponent = null;

		if (selection.numSelected() > 0) {
			if (mb == MouseButton.PRIMARY) {
				for (PaintedRegion region : selection.getSelectedRegions()) {
					if (region.containsPoint(mousex, mousey)) {
						return;
					}
				}
			}
		}
		selection.clearSelected();
		for (Component component : components) {
			if (component.canMove()) {
				if (component.containsPoint(mousex, mousey)) {
					clickedComponent = component;
				}
			}
		}
		if (clickedComponent == null) {
			selection.beginSelecting(mousex, mousey);
		}
		paint();
	}

	private void mouseReleased(int mousex, int mousey, MouseButton mb) {
		this.mouseButtonDown = null;
		selection.setSelecting(false);
		if (clickedComponent != null) {
			if (clickedComponent.containsPoint(mousex, mousey)) {
				if (mb == MouseButton.SECONDARY) {
//					setContextMenu(new CanvasContextMenu(clickedComponent.getText()));
					//					clickedComponent.fireRightClickEvent();
				}
			}
		}
		paint();
	}

	private void mouseMoved(int mousex, int mousey) {
		changeCursorToDefault();
		for (PaintedRegion region : components) {
			if (region.canMove()) {
				if (region.containsPoint(mousex, mousey) && !selection.isSelecting) {
					changeCursorToMove();
					break;
				}
			}
		}
		if (clickedComponent != null && clickedComponent.containsPoint(mousex, mousey)) {
			changeCursorToMove();
		}
		if (mouseButtonDown == null) {
			return;
		}
		if (mouseButtonDown == MouseButton.MIDDLE || (mouseButtonDown == MouseButton.SECONDARY && !selection.isSelecting())) {
			return;
		}
		int dx, dy;
		if (selection.isSelecting()) {
			selection.selectTo(mousex, mousey);
			selection.clearSelected();
			for (PaintedRegion region : components) {
				if (region.canMove()) {
					if (selection.contains(region)) {
						selection.addToSelection(region);
					}
				}
			}
			paint();
			return;
		}
		if (clickedComponent != null) {
			dx = mousex - lastMouseX;
			dy = mousey - lastMouseY;
			clickedComponent.translate(dx, dy);
		} else {
			for (PaintedRegion region : selection.getSelectedRegions()) {
				//only moveable components should be inside selection
				dx = mousex - lastMouseX;
				dy = mousey - lastMouseY;
				region.translate(dx, dy);
			}
		}
		paint();
	}

	private void changeCursorToMove() {
		canvas.setCursor(Cursor.MOVE);
	}

	private void changeCursorToDefault() {
		canvas.setCursor(Cursor.DEFAULT);
	}


	private class CanvasMouseEvent implements EventHandler<MouseEvent> {
		private final UICanvas canvas;

		CanvasMouseEvent(UICanvas canvas) {
			this.canvas = canvas;
		}

		@Override
		public void handle(MouseEvent event) {
			int mousex = (int) event.getSceneX();
			int mousey = (int) event.getSceneY();
			MouseButton btn = event.getButton();
			if (event.getEventType() == MouseEvent.MOUSE_MOVED || event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				canvas.mouseMoved(mousex, mousey);
				canvas.lastMouseX = mousex;
				canvas.lastMouseY = mousey;
				return;
			}
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
				canvas.mousePressed(mousex, mousey, btn);
			} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				canvas.mouseReleased(mousex, mousey, btn);
			}
		}
	}

	private static class Selection extends Region {
		private ArrayList<PaintedRegion> selectedRegions = new ArrayList<>();
		private boolean isSelecting;

		public Selection() {
			super(0, 0, 0, 0);
		}

		void beginSelecting(int x, int y) {
			this.x1 = x;
			this.y1 = y;
			this.x2 = x;
			this.y2 = y;
			this.isSelecting = true;
		}

		void selectTo(int x, int y) {
			this.x2 = x;
			this.y2 = y;
		}

		ArrayList<PaintedRegion> getSelectedRegions() {
			return selectedRegions;
		}

		void addToSelection(PaintedRegion region) {
			this.selectedRegions.add(region);
		}

		boolean removeFromSelection(PaintedRegion region) {
			return this.selectedRegions.remove(region);
		}

		void clearSelected() {
			this.selectedRegions.clear();
		}

		int numSelected() {
			return this.selectedRegions.size();
		}

		boolean isSelecting() {
			return this.isSelecting;
		}

		void setSelecting(boolean selecting) {
			this.isSelecting = selecting;
		}

	}
}
