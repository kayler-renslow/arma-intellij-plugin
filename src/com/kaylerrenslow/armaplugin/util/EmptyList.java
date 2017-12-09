package com.kaylerrenslow.armaplugin.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A list that isn't mutable, but doesn't throw errors when attempting to mutate.
 *
 * @author Kayler
 * @since 12/08/2017
 */
public class EmptyList<T> implements List<T> {

	public static final EmptyList INSTANCE = new EmptyList();

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return Collections.emptyIterator();
	}

	@NotNull
	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	@NotNull
	@Override
	public <T1> T1[] toArray(@NotNull T1[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(T t) {
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends T> c) {
		return false;
	}

	@Override
	public boolean addAll(int index, @NotNull Collection<? extends T> c) {
		return false;
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {

	}

	@Override
	public T get(int index) {
		return null;
	}

	@Override
	public T set(int index, T element) {
		return null;
	}

	@Override
	public void add(int index, T element) {

	}

	@Override
	public T remove(int index) {
		return null;
	}

	@Override
	public int indexOf(Object o) {
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return -1;
	}

	@NotNull
	@Override
	public ListIterator<T> listIterator() {
		return Collections.emptyListIterator();
	}

	@NotNull
	@Override
	public ListIterator<T> listIterator(int index) {
		return Collections.emptyListIterator();
	}

	@NotNull
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return this;
	}
}
