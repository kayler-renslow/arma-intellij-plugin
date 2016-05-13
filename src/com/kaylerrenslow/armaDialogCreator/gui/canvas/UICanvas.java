package com.kaylerrenslow.armaDialogCreator.gui.canvas;

import com.kaylerrenslow.armaDialogCreator.MathUtil;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.Component;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.PaintedRegion;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.Region;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Control;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 @author Kayler
 Created on 05/11/2016. */
public class UICanvas extends Control {

	/** javafx Canvas */
	private final Canvas canvas;
	private final GraphicsContext gc;

	/** width of canvas */
	private final int cwidth,
	/** Height of canvas */
	cheight;

	/** Background of the canvas */
	private Paint background = Color.WHITE;

	/** Color of the mouse selection box */
	private Color selectionColor = Color.GREEN;

	/** All components added */
	private ArrayList<Component> components = new ArrayList<>();

	private Selection selection = new Selection();

	/** Mouse button that is currently down */
	private MouseButton mouseButtonDown = MouseButton.NONE;

	/** Position of where the component context menu was created, relative to the canvas */
	private Point2D contextMenuPosition = new Point2D(-1, -1);

	private int lastMouseX, lastMouseY; //last x and y positions of the mouse relative to the canvas
	private boolean shiftDown = false;
	private boolean ctrlDown = false;

	private IComponentContextMenuCreator menuCreator;

	public UICanvas(int width, int height) {
		this.canvas = new Canvas(width, height);
		this.cwidth = width;
		this.cheight = height;
		this.getChildren().add(this.canvas);

		this.gc = this.canvas.getGraphicsContext2D();

		this.setOnMousePressed(new CanvasMouseEvent(this));
		this.setOnMouseReleased(new CanvasMouseEvent(this));
		this.setOnMouseMoved(new CanvasMouseEvent(this));
		this.setOnMouseDragged(new CanvasMouseEvent(this));

		paint();
	}

	/** Adds a component to the canvas */
	public void addComponent(@NotNull Component component) {
		this.components.add(component);
		paint();
	}

	/**
	 Removes the given component from the canvas render and user interaction

	 @param component component to remove
	 @return true if the component was removed, false if nothing was removed
	 */
	public boolean removeComponent(@NotNull Component component) {
		boolean removed = this.components.remove(component);
		if (removed) {
			paint();
		}
		return removed;
	}

	/**
	 @param ccm the context menu creator that is used to give Components context menus
	 */
	public void setMenuCreator(@NotNull IComponentContextMenuCreator ccm) {
		this.menuCreator = ccm;
	}

	/** Paint the canvas */
	private void paint() {
		gc.setFill(background);
		gc.fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
		for (PaintedRegion paintedRegion : components) {
			boolean select = false;
			for (PaintedRegion selected : selection.getSelected()) {
				if (selected == paintedRegion) {
					select = true;
					break;
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
			gc.setEffect(new DropShadow(region.getWidth() / 2, selectionColor));
		}
		region.paint(gc);
		gc.restore();
	}

	/**
	 Translates the given region by dx and dy only if the given dx and dy won't put the region out of bounds with the canvas. If either put the region out of bounds, the translate won't occur

	 @param r the region to translate
	 @param dx change in x
	 @param dy change in y
	 */
	private void safeTranslate(Region r, int dx, int dy) {
		boolean outX = MathUtil.outOfBounds(r.getLeftX() + dx, 0, cwidth - r.getWidth()) || MathUtil.outOfBounds(r.getRightX() + dx, 0, cwidth);
		if (!outX) {
			boolean outY = MathUtil.outOfBounds(r.getTopY() + dy, 0, cheight - r.getHeight()) || MathUtil.outOfBounds(r.getBottomY() + dy, 0, cheight);
			if (!outY) {
				r.translate(dx, dy);
			}
		}
	}

	private void changeCursorToMove() {
		canvas.setCursor(Cursor.MOVE);
	}

	private void changeCursorToDefault() {
		canvas.setCursor(Cursor.DEFAULT);
	}

	/**
	 This is called when the mouse listener is invoked and a mouse press was the event

	 @param mousex x position of mouse relative to canvas
	 @param mousey y position of mouse relative to canvas
	 @param mb mouse button that was pressed
	 */
	void mousePressed(int mousex, int mousey, @NotNull MouseButton mb) {
		selection.setSelecting(false);
		this.mouseButtonDown = mb;

		boolean clickedSelected = false;
		if (mb == MouseButton.PRIMARY) {
			for (Component component : components) {
				if (!component.isEnabled()) {
					continue;
				}
				if (component.containsPoint(mousex, mousey)) {
					clickedSelected = true;
					if (ctrlDown) {
						selection.toggleFromSelection(component);
					} else {
						if (selection.numSelected() > 0) {
							if (selection.isSelected(component)) {
								break;
							} else {
								selection.clearSelected();
							}
						}
						selection.addToSelection(component);
					}
				}
			}
		}
		if (!clickedSelected) {
			selection.clearSelected();
			selection.beginSelecting(mousex, mousey);
		}
		paint();
	}

	/**
	 This is called when the mouse listener is invoked and a mouse release was the event

	 @param mousex x position of mouse relative to canvas
	 @param mousey y position of mouse relative to canvas
	 @param mb mouse button that was released
	 */
	void mouseReleased(int mousex, int mousey, @NotNull MouseButton mb) {
		this.mouseButtonDown = MouseButton.NONE;
		selection.setSelecting(false);
		setContextMenu(null);
		boolean setContextMenu = false;
		if (selection.getFirst() != null) {
			if (selection.getFirst().containsPoint(mousex, mousey)) {
				if (mb == MouseButton.SECONDARY) {
					setContextMenu = true;
					contextMenuPosition = new Point2D(mousex, mousey);
					if (selection.numSelected() > 1) {
						selection.clearAllButFirst();
					}
				}
			}
		}
		paint();
		if (setContextMenu) {
			setContextMenu(menuCreator.initialize(selection.getFirst()));
		}
	}

	/**
	 This is called when the mouse is moved and/or dragged inside the canvas

	 @param mousex x position of mouse relative to canvas
	 @param mousey y position of mouse relative to canvas
	 */
	void mouseMoved(int mousex, int mousey) {
		changeCursorToDefault();
		for (Component component : components) {
			if (component.isEnabled()) {
				if (component.containsPoint(mousex, mousey)) {
					changeCursorToMove();
					break;
				}
			}
		}
		if (mouseButtonDown == MouseButton.NONE) {
			return;
		}
		if (mouseButtonDown == MouseButton.MIDDLE || (mouseButtonDown == MouseButton.SECONDARY && !selection.isSelecting())) {
			return;
		}
		if (selection.isSelecting()) {
			selection.selectTo(mousex, mousey);
			selection.clearSelected();
			for (Component component : components) {
				if (component.isEnabled()) {
					if (selection.contains(component)) {
						selection.addToSelection(component);
					}
				}
			}
			paint();
			return;
		}
		int dx, dy;
		for (PaintedRegion region : selection.getSelected()) {
			//only moveable components should be inside selection
			dx = mousex - lastMouseX;
			dy = mousey - lastMouseY;
			safeTranslate(region, dx, dy);
		}
		lastMouseX = mousex;
		lastMouseY = mousey;
		paint();
	}

	/**
	 This should be called when any mouse event occurs (press, release, drag, move, etc)

	 @param shiftDown true if the shift key is down, false otherwise
	 @param ctrlDown true if the ctrl key is down, false otherwise
	 */
	void mouseEvent(boolean shiftDown, boolean ctrlDown) {
		this.shiftDown = shiftDown;
		this.ctrlDown = ctrlDown;
	}

	/** Get the position where the context menu was created, relative to canvas */
	@NotNull
	Point2D getContextMenuPosition() {
		return contextMenuPosition;
	}

	/** This is called after mouseMove is called. This will ensure that no matter how mouse move exits, the last mouse position will be updated */
	void setLastMousePosition(int mousex, int mousey) {
		this.lastMouseX = mousex;
		this.lastMouseY = mousey;
	}
}
