package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.data.helper.ResourceFileHelper;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.DimensionType;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.WorldOptions;
import io.github.steaf23.bingoreloaded.lib.api.item.StackBuilderPaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerInfo;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class PaperServer implements PlatformServer {

	private final PlatformCommandDispatcher commandDispatcher = command ->
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

	private final PlatformInventories inventories;
	private final PlatformTaskScheduler taskScheduler;
	private final PaperMenus menus;

	public PaperServer(PlatformTaskScheduler taskScheduler) {
		this.inventories = new PaperInventories();
		this.menus = new PaperMenus(taskScheduler);
		this.taskScheduler = taskScheduler;
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
	public PlatformTaskScheduler taskScheduler() {
		return taskScheduler;
	}

	@Override
	public PaperMenus menus() {
		return menus;
	}

	@Override
	public Collection<? extends PlayerHandle> getOnlinePlayers() {
		return Bukkit.getOnlinePlayers().stream().map(p -> new PlayerHandlePaper(this, p)).toList();
	}

	@Override
	public @Nullable PlayerHandle getPlayerFromUniqueId(UUID id) {
		Player p = Bukkit.getPlayer(id);
		if (p == null) {
			return null;
		}
		return new PlayerHandlePaper(this, p);
	}

	@Override
	public @Nullable PlayerHandle getPlayerFromName(String name) {
		Player p = Bukkit.getPlayer(name);
		if (p == null) {
			return null;
		}
		return new PlayerHandlePaper(this, p);
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
	public Collection<WorldHandle> getLoadedWorlds() {
		return Bukkit.getWorlds().stream().map(this::fromWorld).toList();
	}

	@Override
	public Collection<Key> getAllWorldKeysOnDisk() {
		Path dimensions = Bukkit.getServer().getLevelDirectory().resolve("dimensions");

		if (!Files.isDirectory(dimensions)) {
			return List.of();
		}

		try (Stream<Path> paths = Files.walk(dimensions, 2)) {
			return paths
					.filter(Files::isDirectory)
					.filter(path -> path.getNameCount() == dimensions.getNameCount() + 2)
					.map(path -> Key.key(
							path.getName(path.getNameCount() - 2).toString(),
							path.getFileName().toString()
					))
					.toList();
		} catch (IOException e) {
			return List.of();
		}
	}

	@Override
	public @Nullable WorldHandle getWorld(Key worldName) {
		return fromWorld(Bukkit.getWorld(worldName));
	}

	@Override
	public @Nullable WorldHandle createWorld(WorldOptions options) {
		var creator = WorldCreator.ofKey(NamespacedKey.fromString(options.levelKey().asString()));

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
	public boolean deleteWorld(@NonNull Key worldKey) {
		if (getLoadedWorlds().stream().anyMatch(w -> w.key().equals(worldKey)))
		{
			WorldHandle world = getWorld(worldKey);
			if (world != null && !unloadWorld(world, false)) {
				// Players are still in the world, it could not be unloaded
				ConsoleMessenger.error("Could not remove " + worldKey + ", world could not be unloaded (Maybe there are still players present?).");
				return false;
			}
		}

		Path fullPath = Bukkit.getServer().getLevelDirectory().resolve("dimensions/" + worldKey.namespace() + "/" + worldKey.value());
		if (!ResourceFileHelper.deleteFolderRecurse(fullPath.toString()))
		{
			ConsoleMessenger.bug("Could not remove folder for " + worldKey + ", cannot find the folder of this world (it might already be removed) or the folder could not be accessed", this);
			return false;
		}

		return true;
	}

	@Override
	public StackHandle createItemStackFromTemplate(ItemTemplate template, boolean hideAttributes) {
		return new StackBuilderPaper().buildItem(template, hideAttributes);
	}

	@Override
	public Iterable<AdvancementHandle> allAdvancements() {
		List<AdvancementHandle> handles = new ArrayList<>();
		Bukkit.advancementIterator().forEachRemaining(adv -> handles.add(new AdvancementHandlePaper(adv)));
		return handles;
	}

	private @Nullable WorldHandle fromWorld(@Nullable World world) {
		return world == null ? null : new WorldHandlePaper(this, world);
	}
}
