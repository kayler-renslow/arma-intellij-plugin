package com.kaylerrenslow.armaDialogCreator.gui.canvas.api.action;

/**
 * Created by Kayler on 05/12/2016.
 */
public class ActionEvent {
	private final Object source;

	public ActionEvent(Object source) {
		this.source = source;
	}

	public Object getSource() {
		return source;
	}
}
