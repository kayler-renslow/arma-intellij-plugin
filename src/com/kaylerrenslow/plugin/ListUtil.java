package com.kaylerrenslow.plugin;

import java.util.ArrayList;

/**
 * Created by Kayler on 12/22/2015.
 */
public class ListUtil<E>{

	public java.util.List<E> createList(E...e){
		java.util.List<E> list = new ArrayList<>();
		for(E e1:e){
			list.add(e1);
		}

		return list;
	}
}
