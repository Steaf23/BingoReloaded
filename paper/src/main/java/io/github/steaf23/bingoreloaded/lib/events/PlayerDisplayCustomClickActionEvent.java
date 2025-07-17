package io.github.steaf23.bingoreloaded.lib.events;

import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import net.kyori.adventure.key.Key;

import java.util.UUID;

public class PlayerDisplayCustomClickActionEvent extends PlayerPacketEvent {

	private final Key actionKey;
	private final NBTCompound payload;

	public PlayerDisplayCustomClickActionEvent(UUID userId, Key actionKey, NBTCompound payload) {
		super(userId);
		this.actionKey = actionKey;
		this.payload = payload;
	}

	public Key getActionKey() {
		return actionKey;
	}

	public NBTCompound getPayload() {
		return payload;
	}
}
