package io.github.steaf23.bingoreloadedcompanion.client.hud;

public record HudPlacement(double x, double y, boolean visible, float scaleX, float scaleY, double transparency) {

	public HudPlacement move(double newX, double newY) {
		return new HudPlacement(newX, newY, visible, scaleX, scaleY, transparency);
	}

	public HudPlacement setVisible(boolean value) {
		return new HudPlacement(x, y, value, scaleX, scaleY, transparency);
	}

	public HudPlacement setTransparency(double value) {
		return new HudPlacement(x, y, visible, scaleX, scaleY, value);
	}

	public HudPlacement setScale(float newScaleX, float newScaleY) {
		return new HudPlacement(x, y, visible, newScaleX, newScaleY, transparency);
	}
}
