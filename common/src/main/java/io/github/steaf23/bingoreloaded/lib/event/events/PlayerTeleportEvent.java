package io.github.steaf23.bingoreloaded.lib.event.events;

import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.event.PlayerEvent;

public class PlayerTeleportEvent extends PlayerEvent {

	private final WorldPosition from;
	private final WorldPosition to;

	public PlayerTeleportEvent(PlayerHandle player, WorldPosition from, WorldPosition to) {
		super(player);
		this.from = from;
		this.to = to;
	}

	public WorldPosition toPosition() {
		return to;
	}

	public WorldPosition fromPosition() {
		return from;
	}
}
