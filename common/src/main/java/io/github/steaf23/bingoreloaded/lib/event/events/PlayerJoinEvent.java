package io.github.steaf23.bingoreloaded.lib.event.events;

import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.event.PlayerEvent;

/**
 * Called when a player joins the server
 */
public class PlayerJoinEvent extends PlayerEvent {

	public PlayerJoinEvent(PlayerHandle player) {
		super(player);
	}
}
