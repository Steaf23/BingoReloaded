package io.github.steaf23.bingoreloadedcompanion.client.util;

public class ScreenHelper {

	public static boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
		return pointX >= (double) (x - 1) && pointX < (double) (x + width + 1) && pointY >= (double) (y - 1) && pointY < (double) (y + height + 1);
	}

	public static int addAlphaToColor(int color, int alpha) {
		alpha = alpha << 24;
		return alpha | color;
	}
}
