package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldOptions;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerInfo;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface PlatformServer {

	PlatformCommandDispatcher commandDispatcher();

	Collection<? extends PlayerHandle> getOnlinePlayers();
	@Nullable PlayerHandle getPlayerFromUniqueId(UUID id);
	@Nullable PlayerHandle getPlayerFromName(String name);
	@NotNull PlayerInfo getPlayerInfo(UUID playerId);
	@NotNull PlayerInfo getPlayerInfo(String playerName);

	Collection<WorldHandle> getLoadedWorlds();
	Collection<Key> getAllWorldKeysOnDisk();
	@Nullable WorldHandle getWorld(Key worldKey);
	@Nullable WorldHandle createWorld(WorldOptions options);
	boolean unloadWorld(@NotNull WorldHandle world, boolean save);
	boolean deleteWorld(@NotNull Key worldKey);
}
