package com.kaylerrenslow.armaDialogCreator.arma.util.screen;

/**
 @author Kayler
 Stores screen resolution information and methods for retrieving viewport width and height as well as the viewport x and y positions
 Created on 05/18/2016. */
public class Resolution {
	private int screenWidth, screenHeight;
	private UIScale uiScale;

	public Resolution(ScreenDimension screenDimension, UIScale scale) {
		setScreenDimension(screenDimension);
		setUiScale(scale);
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public UIScale getUiScale() {
		return uiScale;
	}

	public void setScreenDimension(ScreenDimension dimension) {
		this.screenWidth = dimension.width;
		this.screenHeight = dimension.height;
	}

	public void setUiScale(UIScale uiScale) {
		this.uiScale = uiScale;
	}

	public int getViewportWidth() {
		return (int) (screenWidth * 3 / 4 * uiScale.value);
	}

	public int getViewportHeight() {
		return (int) (screenHeight * uiScale.value);
	}

	public int getViewportX() {
		return (screenWidth - getViewportWidth()) / 2;
	}

	public int getViewportY() {
		return (screenHeight - getViewportHeight()) / 2;
	}

	public double getSafeZoneX() {
		return -1.0 * getViewportX() / getViewportWidth();
	}

	public double getSafeZoneY() {
		return -1.0 * getViewportY() / getViewportHeight();
	}

	public double getSafeZoneW() {
		return screenWidth / getViewportWidth();
	}

	public double getSafeZoneH() {
		return screenHeight / getViewportHeight();
	}

	public String toArmaFormattedString() {
		return String.format("[%d,%d,%d,%d,%f,%f]", getScreenWidth(), getScreenHeight(), getViewportWidth(), getViewportHeight(), (getScreenWidth() * 1.0 / getScreenHeight()), uiScale.value);
	}
}
