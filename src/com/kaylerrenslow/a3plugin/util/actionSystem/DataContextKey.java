package com.kaylerrenslow.a3plugin.util.actionSystem;

/**
 * Created by Kayler on 05/08/2016.
 */
public class DataContextKey<E> {
	private final String name;

	public DataContextKey(String name) {
		this.name = name;
	}

	public E getData(ActionDataContext dataContext) {
		return (E) dataContext.getData(this.name);
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
