package io.github.steaf23.bingoreloadedcompanion.client.hud;

public record HudPlacement(int x, int y, boolean visible, int sizeX, int sizeY) {

	public HudPlacement move(int newX, int newY) {
		return new HudPlacement(newX, newY, visible, sizeX, sizeY);
	}

	public HudPlacement setVisible(boolean visible) {
		return new HudPlacement(x, y, visible, sizeX, sizeY);
	}
}
