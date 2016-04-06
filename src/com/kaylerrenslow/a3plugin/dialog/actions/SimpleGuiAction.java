package com.kaylerrenslow.a3plugin.dialog.actions;

/**
 * @author Kayler
 * This interface is used for simple UI control activating. E is the type of data to be passed from the action
 * Created on 04/05/2016.
 */
public interface SimpleGuiAction<E> {
	void actionPerformed(E actionData);
}
