package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.WorldOptions;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerInfo;
import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.kyori.adventure.key.Key;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FabricServer implements PlatformServer {

	private final MinecraftServer server;
	private final PlatformCommandDispatcher commandDispatcher = command -> {};
	private final PlatformInventories inventories = new FabricInventories();

	public FabricServer(MinecraftServer server) {
		this.server = server;
	}

	@Override
	public PlatformInventories inventories() {
		return inventories;
	}

	@Override
	public PlatformCommandDispatcher commandDispatcher() {
		return commandDispatcher;
	}

	@Override
	public Collection<? extends PlayerHandle> getOnlinePlayers() {
		return PlayerLookup.all(server).stream()
				.map(p -> new PlayerHandleFabric(this, p))
				.toList();
	}

	@Override
	public @Nullable PlayerHandle getPlayerFromUniqueId(UUID id) {
		ServerPlayer p = server.getPlayerList().getPlayer(id);
		if (p == null) {
			return null;
		}
		return new PlayerHandleFabric(this, p);
	}

	@Override
	public @Nullable PlayerHandle getPlayerFromName(String name) {
		ServerPlayer p = server.getPlayerList().getPlayer(name);
		if (p == null) {
			return null;
		}
		return new PlayerHandleFabric(this, p);
	}

	@Override
	public @NotNull PlayerInfo getPlayerInfo(UUID playerId) {
		PlayerHandle onlinePlayer = getPlayerFromUniqueId(playerId);
		if (onlinePlayer != null) {
			return new PlayerInfo(playerId, onlinePlayer.playerName());
		}
		return getPlayerInfo(playerId);
	}

	@Override
	public @NotNull PlayerInfo getPlayerInfo(String playerName) {
		PlayerHandle onlinePlayer = getPlayerFromName(playerName);
		if (onlinePlayer != null) {
			return new PlayerInfo(onlinePlayer.uniqueId(), playerName);
		}
		return getPlayerInfo(playerName);
	}

	@Override
	public Collection<WorldHandle> getLoadedWorlds() {
		List<WorldHandle> worlds = new ArrayList<>();
		for (ServerLevel w : server.getAllLevels()) {
			worlds.add(fromWorld(w));
		}
		return worlds;
	}

	@Override
	public Collection<Key> getAllWorldKeysOnDisk() {
		return List.of();
	}

	@Override
	public @Nullable WorldHandle getWorld(Key worldKey) {
		return fromWorld(server.getLevel(ResourceKey.create(Registries.DIMENSION, FabricTypes.idFromKey(worldKey))));
	}

	@Override
	public @Nullable WorldHandle createWorld(WorldOptions options) {
		return null;
	}

	@Override
	public boolean unloadWorld(@NotNull WorldHandle world, boolean save) {
		return false;
	}

	@Override
	public boolean deleteWorld(@NotNull Key worldKey) {
		return false;
	}

	@Override
	public Iterable<AdvancementHandle> allAdvancements() {
		return server.getAdvancements().getAllAdvancements().stream().map(AdvancementHandleFabric::new).collect(Collectors.toSet());
	}

	private @Nullable WorldHandle fromWorld(@Nullable ServerLevel serverWorld) {
		return serverWorld == null ? null : new WorldHandleFabric(serverWorld);
	}

	public MinecraftServer handle() {
		return server;
	}
}
