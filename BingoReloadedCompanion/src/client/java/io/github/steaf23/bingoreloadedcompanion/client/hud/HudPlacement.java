package io.github.steaf23.bingoreloadedcompanion.client.hud;

public record HudPlacement(int x, int y, boolean visible, float scaleX, float scaleY) {

	public HudPlacement move(int newX, int newY) {
		return new HudPlacement(newX, newY, visible, scaleX, scaleY);
	}

	public HudPlacement setVisible(boolean value) {
		return new HudPlacement(x, y, value, scaleX, scaleY);
	}

	public HudPlacement setScale(float newScaleX, float newScaleY) {
		return new HudPlacement(x, y, visible, newScaleX, newScaleY);
	}
}
