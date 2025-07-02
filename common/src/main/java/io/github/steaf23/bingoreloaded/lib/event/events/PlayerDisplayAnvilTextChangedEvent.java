package io.github.steaf23.bingoreloaded.lib.event.events;

import io.github.steaf23.bingoreloaded.lib.event.PlayerEvent;

import java.util.UUID;

public class PlayerDisplayAnvilTextChangedEvent extends PlayerEvent {

	private final String newText;

	public PlayerDisplayAnvilTextChangedEvent(String newText, UUID userId) {
		super(userId);
		this.newText = newText;
	}

	public String getNewText() {
		return newText;
	}
}
