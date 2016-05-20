package com.kaylerrenslow.armaDialogCreator.gui.fx.main;

import com.kaylerrenslow.armaDialogCreator.arma.util.screen.PositionCalculator;
import com.kaylerrenslow.armaDialogCreator.arma.util.screen.Resolution;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.Component;
import com.kaylerrenslow.armaDialogCreator.gui.fx.main.editor.ArmaAbsoluteBoxComponent;
import com.kaylerrenslow.armaDialogCreator.gui.fx.main.editor.UICanvasEditor;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

/**
 Created by Kayler on 05/15/2016.
 */
class CanvasView extends HBox {
	private UICanvasEditor uiCanvasEditor;
	private final CanvasControls canvasControls = new CanvasControls(this);
	private Resolution r;
	private ArmaAbsoluteBoxComponent absRegionComponent;

	CanvasView(Resolution r) {
		initializeUICanvasEditor(r);

		this.getChildren().addAll(uiCanvasEditor, canvasControls);
		HBox.setHgrow(canvasControls, Priority.ALWAYS);

		setOnMouseMoved(new CanvasViewMouseEvent(this));

		focusToCanvas(true);
	}

	private void initializeUICanvasEditor(Resolution r) {
		this.uiCanvasEditor = new UICanvasEditor(r, canvasControls);
		absRegionComponent = new ArmaAbsoluteBoxComponent(r);

		this.uiCanvasEditor.setCanvasContextMenu(new ContextMenu(new MenuItem("Canvas Context Menu")));
		uiCanvasEditor.addComponent(absRegionComponent);
		double safeZoneX = r.getSafeZoneX();
		double safeZoneY = r.getSafeZoneY();
		double safeZoneW = r.getSafeZoneW();
		double safeZoneH = r.getSafeZoneH();
		System.out.println("x:"+safeZoneX + ", y:" + safeZoneY + ", w:" + safeZoneW + ", h:" + safeZoneH);
		System.out.println("vx:"+r.getViewportX()+",vy:" + r.getViewportY());
//		uiCanvasEditor.addComponent(new Component(PositionCalculator.getScreenX(r, safeZoneX), PositionCalculator.getScreenY(r, safeZoneY), PositionCalculator.getScreenWidth(r, safeZoneW), PositionCalculator.getScreenHeight(r, 0.5)));
		Component c = new Component(PositionCalculator.getScreenX(r, 0), PositionCalculator.getScreenY(r, 0), PositionCalculator.getScreenWidth(r, 1), PositionCalculator.getScreenHeight(r, 1));
		c.setBackgroundColor(Color.color(0,1,0,0.7));
		uiCanvasEditor.addComponent(c);
		System.out.println(r.toArmaFormattedString());
		addRandomThings();
	}


	public void repaintCanvas() {
		uiCanvasEditor.paint();
	}

	private void focusToCanvas(boolean focusToCanvas) {
		canvasControls.setFocusTraversable(!focusToCanvas);
		uiCanvasEditor.setFocusTraversable(focusToCanvas);
		if (focusToCanvas) {
			uiCanvasEditor.requestFocus();
		}
	}

	private void addRandomThings() {
		Color[] colors = {Color.RED, Color.BLACK, Color.ORANGE, Color.PURPLE};
		int w = 100;
		int x = (int) uiCanvasEditor.getSnapConfig().alternateSnapPercentage() * 100;
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
			uiCanvasEditor.addComponent(component);
			x += uiCanvasEditor.getSnapConfig().alternateSnapPercentage() * 30;
		}
	}


	void setCanvasSize(int width, int height) {
		this.uiCanvasEditor.setCanvasSize(width, height);
	}


	void keyEvent(String text, boolean keyDown, boolean shiftDown, boolean controlDown, boolean altDown) {
		uiCanvasEditor.keyEvent(text, keyDown, shiftDown, controlDown, altDown);
	}

	void showGrid(boolean showGrid) {
		uiCanvasEditor.showGrid(showGrid);
	}

	void setCanvasBackgroundToImage(String imgPath) {
		uiCanvasEditor.setCanvasBackground(new ImagePattern(new ImageView(imgPath).getImage()));
	}

	void setCanvasBackgroundToColor(Color value) {
		uiCanvasEditor.setCanvasBackground(value);
	}

	void updateCanvasUIColors(Color gridColor, Color selectionColor) {
		uiCanvasEditor.updateCanvasUIColors(gridColor, selectionColor);
	}

	void updateAbsRegion(boolean alwaysFront, boolean showing){
		absRegionComponent.setAlwaysRenderAtFront(alwaysFront);
		absRegionComponent.setGhost(!showing);
		uiCanvasEditor.paint();
	}

	void updateAbsRegionColor(Color c){
		absRegionComponent.setBackgroundColor(c);
		uiCanvasEditor.paint();
	}


	public UICanvasEditor getUiCanvasEditor() {
		return uiCanvasEditor;
	}

	private static class CanvasViewMouseEvent implements EventHandler<MouseEvent> {

		private final CanvasView canvasView;

		CanvasViewMouseEvent(CanvasView canvasView) {
			this.canvasView = canvasView;
		}

		@Override
		public void handle(MouseEvent event) {
			canvasView.focusToCanvas(event.getTarget() == canvasView.uiCanvasEditor.getCanvas());
		}
	}
}
