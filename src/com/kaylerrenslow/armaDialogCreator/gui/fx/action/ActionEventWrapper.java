package com.kaylerrenslow.armaDialogCreator.gui.fx.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 @author Kayler
 Simple utility class that is used with actions and stores a handler and source of the action
 Created on 05/15/2016. */
public class ActionEventWrapper implements EventHandler<ActionEvent> {
	private final IActionEventHandler handler;
	private final Object source;

	public ActionEventWrapper(IActionEventHandler handler, Object source) {
		this.handler = handler;
		this.source = source;
	}

	@Override
	public void handle(ActionEvent event) {
		this.handler.actionPerformed(this.source);
	}
}
