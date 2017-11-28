package com.kaylerrenslow.armaplugin.util;

/**
 * An interface used for lambda's where you need 2 args instead of 1
 *
 * @author Kayler
 * @see java.util.function.Function
 * @since 11/27/2017
 */
public interface DoubleArgFunction<T1, T2, R> {
	R apply(T1 t1, T2 t2);
}
