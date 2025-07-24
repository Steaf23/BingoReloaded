package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackBuilderPaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerInfo;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Registry;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public class PaperServerSoftware implements ServerSoftware {

	private final JavaPlugin plugin;

	public PaperServerSoftware(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public InputStream getResource(String filePath) {
		return plugin.getResource(filePath);
	}

	@Override
	public void saveResource(String name, boolean replace) {
		plugin.saveResource(name, replace);
	}

	@Override
	public File getDataFolder() {
		return plugin.getDataFolder();
	}

	@Override
	public Collection<? extends PlayerHandle> getOnlinePlayers() {
		return Bukkit.getOnlinePlayers().stream().map(PlayerHandlePaper::new).toList();
	}

	@Override
	public @Nullable PlayerHandle getPlayerFromUniqueId(UUID id) {
		return new PlayerHandlePaper(Bukkit.getPlayer(id));
	}

	@Override
	public @Nullable PlayerHandle getPlayerFromName(String name) {
		return new PlayerHandlePaper(Bukkit.getPlayer(name));
	}

	@Override
	public @NotNull PlayerInfo getPlayerInfo(UUID playerId) {
		OfflinePlayer offline = Bukkit.getOfflinePlayer(playerId);
		return new PlayerInfo(playerId, offline.getName());
	}

	@Override
	public @NotNull PlayerInfo getPlayerInfo(String playerName) {
		OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
		return new PlayerInfo(offline.getUniqueId(), playerName);
	}

	@Override
	public ItemType resolveItemType(Key key) {
		return new ItemTypePaper(Registry.MATERIAL.get(key));
	}

	@Override
	public @Nullable DimensionType resolveDimensionType(Key key) {
		if (key.value().equals("overworld")) {
			return DimensionType.OVERWORLD;
		} else if (key.value().equals("nether")) {
			return DimensionType.NETHER;
		} else if (key.value().equals("nether")) {
			return DimensionType.THE_END;
		}

		return null;
	}

	@Override
	public EntityType resolveEntityType(Key key) {
		org.bukkit.entity.EntityType type = Registry.ENTITY_TYPE.get(key);
		if (type == null) {
			return null;
		}
		return new EntityTypePaper(type);
	}

	@Override
	public AdvancementHandle resolveAdvancement(Key key) {
		return new AdvancementHandlePaper(Bukkit.getAdvancement(new NamespacedKey(key.namespace(), key.value())));
	}

	@Override
	public StatisticType resolveStatisticType(Key key) {
		Statistic stat = Registry.STATISTIC.get(key);
		if (stat == null) {
			return null;
		}
		return new StatisticTypePaper(stat);
	}

	@Override
	public ExtensionInfo getExtensionInfo() {
		return new ExtensionInfo(plugin.getPluginMeta().getName(), plugin.getPluginMeta().getVersion(), plugin.getPluginMeta().getAuthors());
	}

	@Override
	public ComponentLogger getComponentLogger() {
		return plugin.getComponentLogger();
	}

	@Override
	public Collection<WorldHandle> getLoadedWorlds() {
		return Bukkit.getWorlds().stream().map(this::fromWorld).toList();
	}

	@Override
	public @Nullable WorldHandle getWorld(String worldName) {
		return fromWorld(Bukkit.getWorld(worldName));
	}

	@Override
	public @Nullable WorldHandle getWorld(UUID worldId) {
		return fromWorld(Bukkit.getWorld(worldId));
	}

	@Override
	public @Nullable WorldHandle createWorld(WorldOptions options) {
		var creator = new WorldCreator(options.name());

		if (options.dimension().equals(DimensionType.OVERWORLD)) {
			creator.environment(World.Environment.NORMAL);
		} else if (options.dimension().equals(DimensionType.NETHER)) {
			creator.environment(World.Environment.NETHER);
		} else if (options.dimension().equals(DimensionType.THE_END)) {
			creator.environment(World.Environment.THE_END);
		} else {
			ConsoleMessenger.bug("Unknown dimension " + options.dimension().key().asString() + " for creating bingo world", this);
		}

		return fromWorld(Bukkit.createWorld(creator));
	}

	@Override
	public boolean unloadWorld(@NotNull WorldHandle world, boolean save) {
		return Bukkit.unloadWorld(((WorldHandlePaper)world).handle(), save);
	}

	@Override
	public StackHandle createStack(ItemType type, int amount) {
		Material mat = ((ItemTypePaper)type).handle();
		return new StackHandlePaper(new ItemStack(mat, amount));
	}

	@Override
	public StackHandle createStackFromBytes(byte[] bytes) {
		return new StackHandlePaper(ItemStack.deserializeBytes(bytes));
	}

	@Override
	public StackHandle createStackFromTemplate(ItemTemplate template, boolean hideAttributes) {
		return new StackBuilderPaper().buildItem(template, hideAttributes);
	}

	@Override
	public byte[] createBytesFromStack(StackHandle stack) {
		return ((StackHandlePaper)stack).handle().serializeAsBytes();
	}

	@Override
	public StatisticHandle createStatistic(StatisticType type, @Nullable ItemType item, @Nullable EntityType entity) {
		return new StatisticHandlePaper((StatisticTypePaper) type, entity, item);
	}

	@Override
	public boolean areAdvancementsDisabled() {
		return !Bukkit.advancementIterator().hasNext() || Bukkit.advancementIterator().next() == null;
	}

	@Override
	public ExtensionTask runTaskTimer(long repeatTicks, long startDelayTicks, Consumer<ExtensionTask> consumer) {
		ExtensionTaskPaper wrapper = new ExtensionTaskPaper();

		Bukkit.getScheduler().runTaskTimer(plugin, (BukkitTask task) -> {
			wrapper.setTask(task);
			consumer.accept(wrapper);
		}, startDelayTicks, repeatTicks);

		return wrapper;
	}

	@Override
	public ExtensionTask runTask(Consumer<ExtensionTask> consumer) {
		ExtensionTaskPaper wrapper = new ExtensionTaskPaper();

		Bukkit.getScheduler().runTask(plugin, (BukkitTask task) -> {
			wrapper.setTask(task);
			consumer.accept(wrapper);
		});

		return wrapper;
	}

	@Override
	public ExtensionTask runTask(long startDelayTicks, Consumer<ExtensionTask> consumer) {
		if (startDelayTicks <= 0) {
			return runTask(consumer);
		}
		else {
			ExtensionTaskPaper wrapper = new ExtensionTaskPaper();

			Bukkit.getScheduler().runTaskLater(plugin, (BukkitTask task) -> {
				wrapper.setTask(task);
				consumer.accept(wrapper);
			}, startDelayTicks);

			return wrapper;
		}
	}

	@Override
	public void sendConsoleCommand(String command) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}

	private @Nullable WorldHandle fromWorld(@Nullable World world) {
		return world == null ? null : new WorldHandlePaper(world);
	}

	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}

	public void saveConfig() {
		plugin.saveConfig();
	}

	public void reloadConfig() {
		plugin.reloadConfig();
	}
}
