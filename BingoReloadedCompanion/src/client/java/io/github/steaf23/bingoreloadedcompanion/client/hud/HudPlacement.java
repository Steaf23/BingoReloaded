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

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof HudPlacement other)) return false;

		return !(Math.abs(this.x() - other.x()) > 0.00001
				|| Math.abs(this.y() - other.y()) > 0.00001
				|| Math.abs(this.scaleX() - other.scaleX()) > 0.00001
				|| Math.abs(this.scaleY() - other.scaleY()) > 0.00001
				|| Math.abs(this.transparency() - other.transparency()) > 0.00001
				|| (this.visible() != other.visible()));
	}
}
