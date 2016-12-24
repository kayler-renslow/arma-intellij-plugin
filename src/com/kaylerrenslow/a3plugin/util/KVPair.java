package com.kaylerrenslow.a3plugin.util;

/**
 * @author Kayler
 * @since 12/30/2015
 */
public class KVPair<K, V> {
	public K key;
	public V value;

	public KVPair(K key, V value) {
		this.key = key;
		this.value = value;
	}
}
