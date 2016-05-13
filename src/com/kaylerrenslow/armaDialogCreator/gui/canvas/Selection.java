package com.kaylerrenslow.armaDialogCreator.gui.canvas;

import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.Component;
import com.kaylerrenslow.armaDialogCreator.gui.canvas.api.ui.Region;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 @author Kayler
 Created on 05/13/2016. */
class Selection extends Region {
	private ArrayList<Component> selected = new ArrayList<>();
	private boolean isSelecting;

	Selection() {
		super(0, 0, 0, 0);
	}

	void beginSelecting(int x, int y) {
		this.x1 = x;
		this.y1 = y;
		this.x2 = x;
		this.y2 = y;
		this.isSelecting = true;
	}

	void selectTo(int x, int y) {
		this.x2 = x;
		this.y2 = y;
	}

	ArrayList<Component> getSelected() {
		return selected;
	}

	@Nullable
	Component getFirst() {
		if (selected.size() == 0) {
			return null;
		}
		return selected.get(0);
	}

	/**
	 Adds or removes the given component from the selection. If the component is selected, it will no longer be selected. If it isn't selected, it will be selected
	 */
	void toggleFromSelection(Component component) {
		if (isSelected(component)) {
			selected.remove(component);
		} else {
			this.selected.add(component);
		}
	}

	void addToSelection(Component component) {
		boolean contains = isSelected(component);
		this.selected.add(component);
	}

	boolean isSelected(Component component) {
		for (Component c : selected) {
			if (c == component) {
				return true;
			}
		}
		return false;
	}

	boolean removeFromSelection(Component component) {
		return this.selected.remove(component);
	}

	void clearSelected() {
		this.selected.clear();
	}

	int numSelected() {
		return this.selected.size();
	}

	boolean isSelecting() {
		return this.isSelecting;
	}

	void setSelecting(boolean selecting) {
		this.isSelecting = selecting;
	}

	void clearAllButFirst() {
		Component first = getFirst();
		clearSelected();
		if (first != null) {
			addToSelection(first);
		}
	}
}
