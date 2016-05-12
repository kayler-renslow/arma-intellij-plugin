package com.kaylerrenslow.armaDialogCreator.gui.lib.ui.api;

/**
 * Created by Kayler on 05/12/2016.
 */
public class Rectangle {

	protected int x1, y1, x2, y2;

	public Rectangle(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public int getTopLeftX() {
		return Math.min(x1, x2);
	}

	public int getTopRightX() {
		return Math.max(x1, x2);
	}

	public int getTopY() {
		return Math.min(y1, y2);
	}

	public int getBottomY() {
		return Math.max(y1, y2);
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	public int getWidth() {
		return Math.abs(x2 - x1);
	}

	public int getHeight() {
		return Math.abs(y2 - y1);
	}

	public void setPosition(Rectangle r) {
		setPosition(r.x1, r.y1, r.x2, r.y2);
	}

	public void setPosition(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public void setPositionWH(int x1, int y1, int width, int height) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x1 + width;
		this.y2 = y1 + height;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}


	public int getCenterX() {
		return (getTopLeftX() - getTopRightX()) / 2;
	}

	public int getCenterY() {
		return (getBottomY() - getTopY()) / 2;
	}

	/**
	 * Translate the region's x and y coordinates relative to the given point
	 *
	 * @param dx change in x
	 * @param dy change in y
	 */
	public void translate(int dx, int dy) {
		this.x1 += dx;
		this.y1 += dy;
		this.x2 += dx;
		this.y2 += dy;
	}

	public boolean containsPoint(int x, int y) {
		if (getTopLeftX() <= x && getTopY() <= y) {
			if (getTopRightX() >= x && getBottomY() >= y) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(Rectangle r){
		if (getTopLeftX() < r.getTopLeftX() && getTopY() < r.getTopY()) {
			if (getTopRightX() > r.getTopRightX() && getBottomY() > r.getBottomY()) {
				return true;
			}
		}
		return false;
	}
}
