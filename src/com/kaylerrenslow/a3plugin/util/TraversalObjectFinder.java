package com.kaylerrenslow.a3plugin.util;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * Generic traversal object discovery interface. Order of operations is found, stopped, and traverseFoundNodesChildren
 * Created on 04/12/2016.
 */
public interface TraversalObjectFinder<E> {
	void found(@NotNull E e);

	/**true if the event holder should stop doing what it is doing, false otherwise*/
	boolean stopped();

	/**True if the children of the found node (from found(E e)) should be read.*/
	boolean traverseFoundNodesChildren();

}
