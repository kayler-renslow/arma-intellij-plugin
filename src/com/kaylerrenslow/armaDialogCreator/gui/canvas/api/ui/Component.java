package com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui;


/**
 * Created by Kayler on 05/12/2016.
 */
public class Component extends PaintedRegion {

	private boolean isEnabled = true;

	public Component(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public void setEnabled(boolean enabled) {
		this.isEnabled = enabled;
	}

}
