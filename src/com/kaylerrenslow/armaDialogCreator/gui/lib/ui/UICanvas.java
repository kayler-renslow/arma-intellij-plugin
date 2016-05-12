package com.kaylerrenslow.armaDialogCreator.gui.lib.ui;

import com.kaylerrenslow.armaDialogCreator.gui.lib.ui.api.Border;
import com.kaylerrenslow.armaDialogCreator.gui.lib.ui.api.PaintedRegion;
import com.kaylerrenslow.armaDialogCreator.gui.lib.ui.api.Rectangle;
import com.kaylerrenslow.armaDialogCreator.gui.lib.ui.control.ContextMenu;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;

/**
 * Created by Kayler on 05/11/2016.
 */
public class UICanvas {

	private final Canvas canvas;
	private final GraphicsContext gc;

	private Paint background = Color.WHITE;
	private Color selectionColor = Color.GREEN;
	private final Border SELECTION_BORDER = new Border(3, Color.RED);

	private ArrayList<PaintedRegion> regions = new ArrayList<>();

	private Selection selection = new Selection();

	private final ContextMenu contextMenu = new ContextMenu(0, 0, 100, 100);

	private MouseButton mouseButtonDown = null;
	private int lastMouseX, lastMouseY;
	private PaintedRegion clickedRegion;

	public UICanvas(int width, int height) {
		this.canvas = new Canvas(width, height);

		this.gc = this.canvas.getGraphicsContext2D();

		initializeListeners();
		paint();
	}

	private void initializeListeners() {
		this.canvas.setOnMousePressed(new CanvasMouseEvent(this));
		this.canvas.setOnMouseReleased(new CanvasMouseEvent(this));
		this.canvas.setOnMouseMoved(new CanvasMouseEvent(this));
		this.canvas.setOnMouseDragged(new CanvasMouseEvent(this));
	}

	public Canvas getCanvasElement() {
		return this.canvas;
	}

	/**
	 * Adds a region to the render
	 */
	public void addRegion(PaintedRegion region) {
		this.regions.add(region);
		paint();
	}

	/**
	 * Removes the given region from the render
	 *
	 * @param region region to add
	 * @return true if the region was removed, false if nothing was removed
	 */
	public boolean removeRegion(PaintedRegion region) {
		boolean removed = this.regions.remove(region);
		if (removed) {
			paint();
		}
		return removed;
	}

	private void paint() {
		gc.setFill(background);
		gc.fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
		for (PaintedRegion paintedRegion : regions) {
			paintRegion(paintedRegion);
		}
		if (selection.isSelecting()) {
			gc.setStroke(selectionColor);
			drawRectangle(selection, false);
		}
		if (contextMenu.isShowing()) {
			paintRegion(contextMenu);
		}
	}

	private void paintRegion(PaintedRegion paintedRegion) {
		gc.save();
		if (paintedRegion.getBorder() != null) {
			gc.save();
			gc.setStroke(paintedRegion.getBorder().getColor());
			gc.setLineWidth(paintedRegion.getBorder().getThickness());
			drawRectangle(paintedRegion, false);
			gc.restore();
		}
		gc.setFill(paintedRegion.getPaint());
		gc.setStroke(paintedRegion.getPaint());
		drawRectangle(paintedRegion, true);
		gc.setFont(paintedRegion.getFont());
		gc.setFill(paintedRegion.getTextColor());
		gc.fillText(paintedRegion.getText(), paintedRegion.getTextX(), paintedRegion.getTextY());
		gc.restore();
	}

	private void drawRectangle(Rectangle r, boolean fill) {
		drawRectangle(r.getX1(), r.getY1(), r.getX2(), r.getY2(), fill);
	}

	private void drawRectangle(int x1, int y1, int x2, int y2, boolean fill){
		final double antiAlias = 0.5;
		gc.beginPath();
		gc.moveTo(x1 + antiAlias, y1 + antiAlias);
		gc.lineTo(x2 + antiAlias, y1 + antiAlias); // top right
		gc.lineTo(x2 + antiAlias, y2 + antiAlias); // bottom right
		gc.lineTo(x1 + antiAlias, y2 + antiAlias); // bottom left
		gc.lineTo(x1 + antiAlias, y1 + antiAlias); // top left
		gc.closePath();
		gc.stroke();
		if (fill) {
			gc.fill();
		}
	}

	private void mousePressed(int mousex, int mousey, MouseButton mb) {
		selection.setSelecting(false);
		this.mouseButtonDown = mb;
		if(selection.numSelected() > 0){
			if(mb == MouseButton.PRIMARY){
				for(PaintedRegion region : selection.getSelectedRegions()){
					if(region.containsPoint(mousex, mousey)){
						return;
					}
				}
			}
		}
		selection.clearSelected();
		clickedRegion = null;
		contextMenu.setShowing(false);
		for (PaintedRegion region : regions) {
			if (region.containsPoint(mousex, mousey)) {
				clickedRegion = region;
				region.setBorder(SELECTION_BORDER);
			} else {
				region.setBorder(null);
			}
		}
		if (clickedRegion == null) {
			selection.beginSelecting(mousex, mousey);
		}
		paint();
	}

	private void mouseReleased(int mousex, int mousey, MouseButton mb) {
		this.mouseButtonDown = null;
		if (selection.isSelecting()) {
			for (PaintedRegion region : regions) {
				if (selection.contains(region)) {
					selection.addToSelection(region);
					region.setBorder(SELECTION_BORDER);
				}
			}
		}
		selection.setSelecting(false);
		if (mb == MouseButton.PRIMARY) {
			contextMenu.setShowing(false);
		} else if (mb == MouseButton.SECONDARY) {
			contextMenu.setShowing(true);
			contextMenu.setPositionWH(mousex, mousey, contextMenu.getWidth(), contextMenu.getHeight());
		}
		if (clickedRegion != null) {
			if (clickedRegion.containsPoint(mousex, mousey)) {
				if (mb == MouseButton.SECONDARY) {
					System.out.println("UICanvas.mouseReleased");
				}
			} else {
				clickedRegion.setBorder(null);
			}
		}
		paint();
	}

	private void mouseMoved(int mousex, int mousey) {
		if (mouseButtonDown == null) {
			return;
		}
		if (mouseButtonDown == MouseButton.MIDDLE) {
			return;
		}
		int dx, dy;
		if (selection.isSelecting()) {
			selection.selectTo(mousex, mousey);
			paint();
			return;
		}
		if (clickedRegion != null) {
			dx = mousex - lastMouseX;
			dy = mousey - lastMouseY;
			clickedRegion.translate(dx, dy);
		} else {
			for (PaintedRegion region : selection.getSelectedRegions()) {
				dx = mousex - lastMouseX;
				dy = mousey - lastMouseY;
				region.translate(dx, dy);
			}
		}
		lastMouseX = mousex;
		lastMouseY = mousey;
		paint();
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
				lastMouseX = mousex;
				lastMouseY = mousey;
				return;
			}
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
				canvas.mousePressed(mousex, mousey, btn);
			} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				canvas.mouseReleased(mousex, mousey, btn);
			}
		}
	}


}
