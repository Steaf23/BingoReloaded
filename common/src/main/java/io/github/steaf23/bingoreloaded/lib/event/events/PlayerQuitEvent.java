package io.github.steaf23.bingoreloaded.lib.event.events;

import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.event.PlayerEvent;

public class PlayerQuitEvent extends PlayerEvent {

	public PlayerQuitEvent(PlayerHandle player) {
		super(player);
	}
}
