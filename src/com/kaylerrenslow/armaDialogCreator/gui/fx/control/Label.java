package com.kaylerrenslow.armaDialogCreator.gui.fx.control;

import javafx.scene.Node;

/**
 I'm tired of label not showing up in content assist.
 */
public class Label extends javafx.scene.control.Label{
	public Label() {
	}

	public Label(String text) {
		super(text);
	}

	public Label(String text, Node graphic) {
		super(text, graphic);
	}
}
