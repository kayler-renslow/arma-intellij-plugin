package com.kaylerrenslow.a3plugin.util;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Kayler on 03/30/2016.
 */
public class Attribute {
	public final String name;
	public final String value;

	public Attribute(@NotNull String name, @NotNull String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String toString() {
		return "name=" + name + ", value=" + value;
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof Attribute){
			Attribute a = (Attribute)o;
			return a.value.equals(this.value) && a.name.equals(this.name);
		}
		return false;
	}

}
