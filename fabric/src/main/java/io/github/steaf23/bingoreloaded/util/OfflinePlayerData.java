package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.lib.api.player.PlayerInfo;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public class OfflinePlayerData {

	private final MinecraftServer server;

	public OfflinePlayerData(MinecraftServer server) {
		this.server = server;
	}

	public PlayerInfo getOfflinePlayer(UUID playerId) {

	}

	public PlayerInfo getOfflinePlayer(String playerName) {

	}
}
