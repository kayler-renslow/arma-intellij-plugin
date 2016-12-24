package com.kaylerrenslow.a3plugin.dialog.util;

import java.awt.*;

/**
 * @author Kayler
 * @since 04/27/2016
 */
public class DialogUtil {

	public static Component getHighestAncestor(Component component){
		while (component.getParent() != null) {
			component = component.getParent();
		}
		return component;
	}
}
