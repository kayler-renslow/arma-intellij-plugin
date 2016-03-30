package com.kaylerrenslow.a3plugin.dialog.util;

/**
 * @author Kayler
 * Created on 12/30/2015.
 */
public interface DialogActionResponder<E, T>{

	/**Called when an action is performed on an object. T moreInfo can be anything.*/
	void actionPerformed(E obj, T moreInfo);
}
