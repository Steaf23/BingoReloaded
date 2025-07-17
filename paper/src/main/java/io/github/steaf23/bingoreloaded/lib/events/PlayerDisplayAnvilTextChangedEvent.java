package io.github.steaf23.bingoreloaded.lib.events;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerDisplayAnvilTextChangedEvent extends PlayerPacketEvent {

	private final String newText;

	public PlayerDisplayAnvilTextChangedEvent(String newText, UUID userId) {
		super(userId);
		this.newText = newText;
	}

	public String getNewText() {
		return newText;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return null;
	}
}
