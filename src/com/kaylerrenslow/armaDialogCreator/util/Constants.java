package com.kaylerrenslow.armaDialogCreator.util;

import java.awt.*;

/**
 Created by Kayler on 05/15/2016.
 */
public class Constants {

	public enum CanvasDimension {

		D640(640, 360),
		D720(720, 405),
		D848(848, 480),
		D960(960, 540),
		D1024(1024, 576),
		D1280(1280, 720),
		D1366(1366, 768),
		D1600(1600, 900);

		public final int width;
		public final int height;

		CanvasDimension(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}

}
