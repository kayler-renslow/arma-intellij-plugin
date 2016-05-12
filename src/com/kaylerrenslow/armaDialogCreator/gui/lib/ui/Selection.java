package com.kaylerrenslow.armaDialogCreator.gui.lib.ui;

import com.kaylerrenslow.armaDialogCreator.gui.lib.ui.api.PaintedRegion;
import com.kaylerrenslow.armaDialogCreator.gui.lib.ui.api.Rectangle;

import java.util.ArrayList;

/**
 * Created by Kayler on 05/12/2016.
 */
class Selection extends Rectangle {
	private ArrayList<PaintedRegion> selectedRegions = new ArrayList<>();
	private boolean isSelecting;

	public Selection() {
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

	ArrayList<PaintedRegion> getSelectedRegions() {
		return selectedRegions;
	}

	void addToSelection(PaintedRegion region) {
		this.selectedRegions.add(region);
	}

	boolean removeFromSelection(PaintedRegion region) {
		return this.selectedRegions.remove(region);
	}

	void clearSelected() {
		this.selectedRegions.clear();
	}

	int numSelected() {
		return this.selectedRegions.size();
	}

	boolean isSelecting() {
		return this.isSelecting;
	}

	void setSelecting(boolean selecting) {
		this.isSelecting = selecting;
	}

	PaintedRegion getFirstSelected() {
		return selectedRegions.get(0);
	}
}
