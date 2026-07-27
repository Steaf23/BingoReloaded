package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypeFabric;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerInfo;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.util.FabricTypes;
import io.github.steaf23.bingoreloaded.util.OfflinePlayerData;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.Person;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class FabricServerSoftware implements ServerSoftware {

	private final MinecraftServer server;
	private final String mod;

	private final OfflinePlayerData playerData;

	private final @NotNull ExtensionInfo info;

	public FabricServerSoftware(MinecraftServer server, String modId) {
		this.server = server;
		this.mod = modId;
		this.playerData = new OfflinePlayerData(server);

		ModContainer container = FabricLoader.getInstance().getModContainer(modId).orElse(null);
		assert container != null;

		if (container == null) {
			this.info = null;
			return;
		}

		List<String> authors = container.getMetadata().getAuthors().stream()
				.map(Person::getName)
				.toList();

		this.info = new ExtensionInfo(container.getMetadata().getName(), container.getMetadata().getVersion().getFriendlyString(), authors);
	}

	@Override
	public InputStream getResource(String filePath) {
		// Using the class of your mod initializer
		return FabricServerSoftware.class.getClassLoader().getResourceAsStream(filePath);
	}

	@Override
	public void saveResource(String name, boolean replace) {
		// copy from jar if missing
		if (!replace && Files.exists(getDataFolder().toPath().resolve(name))) {
			return;
		}

		InputStream in = getResource(name);
		if (in == null) {
			ConsoleMessenger.bug("Could not locate the file at " + name, this);
			return;
		}

		try  {
			Files.createDirectories(getDataFolder().toPath().resolve(name).getParent());
			Files.copy(in, getDataFolder().toPath().resolve(name), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException exception) {
			ConsoleMessenger.bug(exception.toString(), this);
			ConsoleMessenger.bug("Could not save the file at " + getDataFolder().toPath().resolve(name) + " from " + name, this);
		}
	}

	@Override
	public File getDataFolder() {
		Path configDir = FabricLoader.getInstance().getConfigDir();
		configDir = configDir.resolve(mod);
		return configDir.toFile();
	}

	@Override
	public Collection<? extends PlayerHandle> getOnlinePlayers() {
		return PlayerLookup.all(server).stream()
				.map(PlayerHandleFabric::new)
				.toList();
	}

	@Override
	public @Nullable PlayerHandle getPlayerFromUniqueId(UUID id) {
		ServerPlayer p = server.getPlayerList().getPlayer(id);
		if (p == null) {
			return null;
		}
		return new PlayerHandleFabric(p);
	}

	@Override
	public @Nullable PlayerHandle getPlayerFromName(String name) {
		ServerPlayer p = server.getPlayerList().getPlayer(name);
		if (p == null) {
			return null;
		}
		return new PlayerHandleFabric(p);
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
	public ItemType resolveItemType(Key key) {
		return new ItemTypeFabric(BuiltInRegistries.ITEM.getValue(FabricTypes.idFromKey(key)));
	}

	@Override
	public DimensionType resolveDimensionType(Key key) {
		if (key.value().equals("overworld")) {
			return DimensionType.OVERWORLD;
		} else if (key.value().equals("nether")) {
			return DimensionType.NETHER;
		} else if (key.value().equals("the_end")) {
			return DimensionType.THE_END;
		}

		return null;
	}

	@Override
	public EntityType resolveEntityType(Key key) {
		net.minecraft.world.entity.EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(FabricTypes.idFromKey(key));
		if (type == EntityTypes.PIG && !key.value().equals("pig")) { // MC chose pig as default for some reason...
			return null;
		}
		return new EntityTypeFabric(type);
	}

	@Override
	public AdvancementHandle resolveAdvancement(Key key) {
		return new AdvancementHandleFabric(FabricTypes.idFromKey(key), server);
	}

	@Override
	public StatisticType resolveStatisticType(Key key) {
		StatType<?> statType = BuiltInRegistries.STAT_TYPE.getValue(FabricTypes.idFromKey(key));
		if (statType == null) {
			return null;
		}
		return new StatisticTypeFabric(statType);
	}

	@Override
	public StatusEffectType resolvePotionEffectType(Key key) {
		MobEffect type = BuiltInRegistries.MOB_EFFECT.getValue(FabricTypes.idFromKey(key));
		if (type == null) {
			return null;
		}
		return new StatusEffectTypeFabric(type);
	}

	@Override
	public ExtensionInfo getExtensionInfo() {
		return info;
	}

	@Override
	public ComponentLogger getComponentLogger() {
		return ComponentLogger.logger(mod);
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
	public StackHandle createStack(ItemType type, int amount) {
		return null;
	}

	@Override
	public StackHandle createStackFromBytes(byte[] bytes) {
		return null;
	}

	@Override
	public StackHandle createStackFromTemplate(ItemTemplate template, boolean hideAttributes) {
		return null;
	}

	@Override
	public byte[] createBytesFromStack(StackHandle stack) {
		return new byte[0];
	}

	@Override
	public StackHandle colorItemStack(StackHandle stack, TextColor color) {
		return null;
	}

	@Override
	public StatisticHandle createStatistic(StatisticType type, @Nullable ItemType item, @Nullable EntityType entity) {
		return null;
	}

	@Override
	public boolean areAdvancementsDisabled() {
		return false;
	}

	@Override
	public ExtensionTask runTaskTimer(long repeatTicks, long startDelayTicks, Consumer<ExtensionTask> consumer) {
		return null;
	}

	@Override
	public ExtensionTask runTask(Consumer<ExtensionTask> consumer) {
		return null;
	}

	@Override
	public ExtensionTask runTask(long startDelayTicks, Consumer<ExtensionTask> consumer) {
		return null;
	}

	@Override
	public void sendConsoleCommand(String command) {

	}

	public String modId() {
		return mod;
	}

	private @Nullable WorldHandle fromWorld(@Nullable ServerLevel serverWorld) {
		return serverWorld == null ? null : new WorldHandleFabric(serverWorld);
	}
}
