package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.DimensionType;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.ExtensionInfo;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerInfo;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.ExtensionTask;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldOptions;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class FabricServerSoftware implements ServerSoftware {

	@Override
	public InputStream getResource(String filePath) {
		return null;
	}

	@Override
	public void saveResource(String name, boolean replace) {

	}

	@Override
	public File getDataFolder() {
		return null;
	}

	@Override
	public Collection<? extends PlayerHandle> getOnlinePlayers() {
		return List.of();
	}

	@Override
	public @Nullable PlayerHandle getPlayerFromUniqueId(UUID id) {
		return null;
	}

	@Override
	public @Nullable PlayerHandle getPlayerFromName(String name) {
		return null;
	}

	@Override
	public @NotNull PlayerInfo getPlayerInfo(UUID playerId) {
		return null;
	}

	@Override
	public @NotNull PlayerInfo getPlayerInfo(String playerName) {
		return null;
	}

	@Override
	public ItemType resolveItemType(Key key) {
		return null;
	}

	@Override
	public DimensionType resolveDimensionType(Key key) {
		return null;
	}

	@Override
	public EntityType resolveEntityType(Key key) {
		return null;
	}

	@Override
	public AdvancementHandle resolveAdvancement(Key key) {
		return null;
	}

	@Override
	public StatisticType resolveStatisticType(Key key) {
		return null;
	}

	@Override
	public ExtensionInfo getExtensionInfo() {
		return null;
	}

	@Override
	public ComponentLogger getComponentLogger() {
		return null;
	}

	@Override
	public Collection<WorldHandle> getLoadedWorlds() {
		return List.of();
	}

	@Override
	public @Nullable WorldHandle getWorld(String worldName) {
		return null;
	}

	@Override
	public @Nullable WorldHandle getWorld(UUID worldName) {
		return null;
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
}
