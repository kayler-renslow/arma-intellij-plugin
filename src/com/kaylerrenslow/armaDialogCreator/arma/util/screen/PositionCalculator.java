package com.kaylerrenslow.armaDialogCreator.arma.util.screen;

/**
 Created by Kayler on 05/18/2016.
 */
public class PositionCalculator {
	public static int getScreenX(Resolution resolution, double percentX) {
		return (int) (resolution.getViewportX() + percentX * resolution.getViewportWidth());
	}

	public static int getScreenY(Resolution resolution, double percentY) {
		return (int) (resolution.getViewportY() + percentY * resolution.getViewportHeight());
	}

	public static int getScreenWidth(Resolution resolution, double percentW){
		return (int) (resolution.getViewportWidth() * percentW);
	}

	public static int getScreenHeight(Resolution resolution, double percentH){
		return (int) (resolution.getViewportHeight() * percentH);
	}
}
