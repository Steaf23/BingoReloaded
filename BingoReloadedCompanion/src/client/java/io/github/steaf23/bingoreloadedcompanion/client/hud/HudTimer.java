package io.github.steaf23.bingoreloadedcompanion.client.hud;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class HudTimer implements ClientTickEvents.EndTick {

	private static long tick = 0;

	@Override
	public void onEndTick(Minecraft minecraftClient) {
		tick++;
	}

	public static long getTicks() {
		return tick;
	}
}
