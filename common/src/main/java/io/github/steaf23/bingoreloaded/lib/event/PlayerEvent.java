package io.github.steaf23.bingoreloaded.lib.event;

import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;

public abstract class PlayerEvent extends Event {

	private final PlayerHandle player;

	public PlayerEvent(PlayerHandle player) {
		this.player = player;
	}

	public PlayerHandle player() {
		return player;
	}
}
