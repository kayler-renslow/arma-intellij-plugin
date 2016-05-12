package com.kaylerrenslow.armaDialogCreator.gui.lib.ui.control;

import com.kaylerrenslow.armaDialogCreator.gui.lib.ui.api.PaintedRegion;
import javafx.scene.paint.Color;

/**
 * Created by Kayler on 05/11/2016.
 */
public class ContextMenu extends PaintedRegion {

	private boolean showing;

	public ContextMenu(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.myPaint = Color.GREEN;
	}

	public void setShowing(boolean showing){
		this.showing = showing;
	}

	public boolean isShowing() {
		return showing;
	}
}
