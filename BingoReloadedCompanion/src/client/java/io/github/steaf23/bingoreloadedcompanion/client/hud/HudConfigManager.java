package io.github.steaf23.bingoreloadedcompanion.client.hud;

import net.minecraft.util.Identifier;

public class HudConfigManager {

	public record Rect(int x, int y, int width, int height) {

		public int endX() {
			return x + width;
		}

		public int endY() {
			return y + height;
		}
	}

	public Rect getUsedRectOfElement(Identifier id) {
		HudInfo info = ConfigurableHudRegistry.getInfo(id);
		HudPlacement placement = ConfigurableHudRegistry.getDefaultPlacement(id);

		if (info == null || placement == null) {
			return new Rect(0, 0, 0, 0);
		}

		return new Rect(placement.x(), placement.y(),
				Math.max(info.minSizeX(), placement.sizeX()),
				Math.max(info.minSizeY(), placement.sizeY()));
	}

	public HudPlacement getHudPlacement(Identifier id) {
		return ConfigurableHudRegistry.getDefaultPlacement(id);
	}
}
