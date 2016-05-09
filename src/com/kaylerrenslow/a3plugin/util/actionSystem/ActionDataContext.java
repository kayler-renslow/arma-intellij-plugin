package com.kaylerrenslow.a3plugin.util.actionSystem;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Created by Kayler on 05/08/2016.
 */
public class ActionDataContext {
	private final HashMap<String, Object> map = new HashMap<>();

	@Nullable
	public Object getData(String key){
		return map.get(key);
	}

	/** Puts a value inside the context. If a value exists for the given key, the old value is replaced
	 * @param key key
	 * @param o value
	 */
	public void putData(DataContextKey key, Object o){
		this.map.put(key.getName(), o);
	}

}
