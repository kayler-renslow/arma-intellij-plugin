package com.kaylerrenslow.armaDialogCreator.gui.canvas;

import com.kaylerrenslow.armaDialogCreator.MathUtil;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.Component;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.PaintedRegion;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.Region;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Control;
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

	/** Color of the grid */
	private Color gridColor = Color.GRAY;

	/** All components added */
	private ArrayList<Component> components = new ArrayList<>();

	private Selection selection = new Selection();

	/** Mouse button that is currently down */
	private MouseButton mouseButtonDown = MouseButton.NONE;

	/** Position of where the component context menu was created, relative to the canvas */
	private Point2D contextMenuPosition = new Point2D(-1, -1);

	private int lastMouseX, lastMouseY; //last x and y positions of the mouse relative to the canvas
	private int dxAmount, dyAmount = 0; //amount of change that has happened since last snap

	private boolean shiftDown, altDown, ctrlDown;

	private IComponentContextMenuCreator menuCreator;
	private IPositionCalculator calc;


	public UICanvas(int width, int height, IComponentContextMenuCreator contextMenuCreator, IPositionCalculator calculator) {
		this.canvas = new Canvas(width, height);
		this.cwidth = width;
		this.cheight = height;
		setMenuCreator(contextMenuCreator);
		setPositionCalculator(calculator);
		this.gc = this.canvas.getGraphicsContext2D();


		this.getChildren().add(this.canvas);

		this.setOnMousePressed(new CanvasMouseEvent(this));
		this.setOnMouseReleased(new CanvasMouseEvent(this));
		this.setOnMouseMoved(new CanvasMouseEvent(this));
		this.setOnMouseDragged(new CanvasMouseEvent(this));

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				paint();
			}
		}.start();
	}

	public int getCanvasWidth() {
		return this.cwidth;
	}

	public int getCanvasHeight() {
		return this.cheight;
	}

	/** Adds a component to the canvas */
	public void addComponent(@NotNull Component component) {
		this.components.add(component);
	}

	/**
	 Removes the given component from the canvas render and user interaction

	 @param component component to remove
	 @return true if the component was removed, false if nothing was removed
	 */
	public boolean removeComponent(@NotNull Component component) {
		boolean removed = this.components.remove(component);
		return removed;
	}


	public void setPositionCalculator(@NotNull IPositionCalculator positionCalculator) {
		this.calc = positionCalculator;
	}

	@NotNull
	public IPositionCalculator getPositionCalculator() {
		return this.calc;
	}

	/**
	 @param ccm the context menu creator that is used to give Components context menus
	 */
	public void setMenuCreator(@NotNull IComponentContextMenuCreator ccm) {
		this.menuCreator = ccm;
	}

	/** Paint the canvas */
	private void paint() {
		gc.save();
		gc.setFill(background);
		gc.fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
		drawGrid();
		for (Component component : components) {
			boolean select = false;
			for (Component selected : selection.getSelected()) {
				if (selected == component) {
					select = true;
					break;
				}
			}
			paintComponent(component, select);
		}

		if (selection.isSelecting()) {
			gc.save();
			gc.setStroke(selectionColor);
			gc.setLineWidth(2);
			selection.drawRectangle(gc);
			gc.restore();
		}
		gc.restore();
	}

	private void drawGrid() {
		double spacing = calc.getGridScale() * calc.snapAmount();
		if (spacing <= 0.001) {
			return;
		}
		int numX = cwidth / (int) spacing;
		int numY = cheight / (int) spacing;
		double yy, xx;
		double antiAlias = 0.5;
		gc.save();
		gc.setStroke(gridColor);
		for (int y = 0; y < numY; y++) {
			yy = y * spacing + antiAlias;
			gc.strokeLine(0, yy, cwidth, yy);
			for (int x = 0; x < numX; x++) {
				xx = x * spacing + antiAlias;
				gc.strokeLine(xx, 0, xx, cheight);
			}
		}
		gc.restore();
	}

	private void paintComponent(Component component, boolean select) {
		gc.save();
		if (select) {
			gc.save();
			gc.setGlobalAlpha(0.6);
			gc.setStroke(selectionColor);
			int offset = 4 + (component.getBorder() != null ? component.getBorder().getThickness() : 0);
			Region.fillRectangle(gc, component.getLeftX() - offset, component.getTopY() - offset, component.getRightX() + offset, component.getBottomY() + offset);
			gc.restore();
		}
		component.paint(gc);
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
		System.out.println("UICanvas.mousePressed");
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
		if (mb == MouseButton.SECONDARY) {
			return;
		}
		if (!clickedSelected) {
			selection.clearSelected();
			selection.beginSelecting(mousex, mousey);
		}

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
		if (mb == MouseButton.SECONDARY) {
			for (Component selected : selection.getSelected()) {
				if (selected.containsPoint(mousex, mousey)) {
					setContextMenu = true;
					contextMenuPosition = new Point2D(mousex, mousey);
					if (selection.numSelected() > 1) {
						selection.removeAllExcept(selected);
					}
					break;
				}
			}
		}
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
			return;
		}
		int dx = mousex - lastMouseX; //change in x
		int dy = mousey - lastMouseY; //change in y
		int dx1 = 0;
		int dy1 = 0; //change in x,y that will be used for translation
		int ddx = dx < 0 ? -1 : 1; //change in direction for x
		int ddy = dy < 0 ? -1 : 1; //change in direction for y
		int snap = calc.snapAmount();

		if (calc.snapEnabled() && !shiftDown) {
			dxAmount += dx;
			dyAmount += dy;
			int dxAmountAbs = Math.abs(dxAmount);
			int dyAmountAbs = Math.abs(dyAmount);
			if (dxAmountAbs >= snap) {
				dx1 = snap * ddx + snap * ddx * (dxAmountAbs / snap - 1);
				dxAmount = dxAmountAbs % snap;
			}
			if (dyAmountAbs >= snap) {
				dy1 = snap * ddy + snap * ddy * (dyAmountAbs / snap - 1);
				dyAmount = dyAmountAbs % snap;
			}
		} else {
			dxAmount = 0; //if snap is enabled later, we won't want to have the old data inside
			dyAmount = 0;
			dx1 = dx;
			dy1 = dy;
		}
		int moveX, moveY, nearestGridX, nearestGridY;
		for (PaintedRegion region : selection.getSelected()) {
			//only moveable components should be inside selection
			if (altDown) { //put this somewhere else so that the action makes sense
				moveX = region.getLeftX();
				moveY = region.getTopY();
				nearestGridX = moveX - moveX % snap;
				nearestGridY = moveY - moveY % snap;
				dx1 = nearestGridX - moveX;
				dy1 = nearestGridY - moveY;
			}
			safeTranslate(region, dx1, dy1);
		}
	}

	private void printCoord(int x, int y) {
		System.out.println(x + "," + y);
	}

	/**
	 This should be called when any mouse event occurs (press, release, drag, move, etc)

	 @param shiftDown true if the shift key is down, false otherwise
	 @param ctrlDown true if the ctrl key is down, false otherwise
	 @param altDown true if alt key is down, false otherwise
	 */
	void mouseEvent(boolean shiftDown, boolean ctrlDown, boolean altDown) {
		this.shiftDown = shiftDown;
		this.ctrlDown = ctrlDown;
		this.altDown = altDown;
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
