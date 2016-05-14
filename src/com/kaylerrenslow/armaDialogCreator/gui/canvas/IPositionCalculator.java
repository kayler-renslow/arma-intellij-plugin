package com.kaylerrenslow.armaDialogCreator.gui.canvas;

/**
 Created by Kayler on 05/13/2016.
 */
public interface IPositionCalculator {
	/**Get how many times bigger the grid spacing is compared to the snap. If grid scale is <=0, the grid won't show.*/
	double getGridScale();

	boolean snapEnabled();

	/**
	 How much snap there is. Should be >= 0
	 */
	int snapAmount();
}
