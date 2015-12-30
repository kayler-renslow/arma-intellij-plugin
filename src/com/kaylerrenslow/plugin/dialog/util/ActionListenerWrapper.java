package com.kaylerrenslow.plugin.dialog.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Kayler on 12/30/2015.
 */
public class ActionListenerWrapper<E, T> implements ActionListener{
	private E obj;
	private DialogActionResponder<E, T> responder;
	private T moreInfo;

	public ActionListenerWrapper(DialogActionResponder<E, T> responder, E obj, T moreInfo) {
		this.responder = responder;
		this.obj = obj;
		this.moreInfo = moreInfo;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		responder.actionPerformed(obj, moreInfo);
	}
}
