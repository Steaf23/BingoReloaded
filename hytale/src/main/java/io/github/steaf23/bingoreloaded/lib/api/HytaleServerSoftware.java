package io.github.steaf23.bingoreloaded.lib.api;

import com.hypixel.hytale.common.plugin.AuthorInfo;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypeHytale;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandleHytale;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleHytale;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerInfo;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.LoggerWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class HytaleServerSoftware implements ServerSoftware {

	private final JavaPlugin plugin;
	private final Universe universe = Universe.get();

	public HytaleServerSoftware(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public @Nullable InputStream getResource(String filePath) {
		if (filePath == null) {
			throw new IllegalArgumentException("Filename cannot be null");
		}

		try {
			URL url = plugin.getClassLoader().getResource(filePath);

			if (url == null) {
				return null;
			}

			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		} catch (IOException ex) {
			return null;
		}
	}

	@Override
	public void saveResource(String name, boolean replace) {
		if (name == null || name.equals("")) {
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
		}

		name = name.replace('\\', '/');
		InputStream in = getResource(name);
		if (in == null) {
			throw new IllegalArgumentException("The embedded resource '" + name + "' cannot be found in " + plugin.getFile());
		}

		File outFile = new File(getDataFolder(), name);
		int lastIndex = name.lastIndexOf('/');
		File outDir = new File(getDataFolder(), name.substring(0, lastIndex >= 0 ? lastIndex : 0));

		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		try {
			if (!outFile.exists() || replace) {
				OutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			} else {
			}
		} catch (IOException ex) {

		}
	}

	@Override
	public File getDataFolder() {
		return plugin.getDataDirectory().toFile();
	}

	@Override
	public Collection<? extends PlayerHandle> getOnlinePlayers() {
		return List.of();
	}

	@Override
	public @Nullable PlayerHandle getPlayerFromUniqueId(UUID id) {
		PlayerRef player = Universe.get().getPlayer(id);
		if (player == null) {
			return null;
		}
		return new PlayerHandleHytale(player);
	}

	@Override
	public @Nullable PlayerHandle getPlayerFromName(String name) {
		PlayerRef player = Universe.get().getPlayerByUsername(name, NameMatching.EXACT_IGNORE_CASE);
		if (player == null) {
			return null;
		}
		return new PlayerHandleHytale(player);
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
		return new ItemTypeHytale(key.toString());
	}

	@Override
	public ItemType resolveItemType(String key) {
		return new ItemTypeHytale(key);
	}

	@Override
	public DimensionType resolveDimensionType(Key key) {
		return DimensionType.OVERWORLD;
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
	public StatusEffectType resolvePotionEffectType(Key key) {
		return null;
	}

	@Override
	public ExtensionInfo getExtensionInfo() {
		return new ExtensionInfo(plugin.getName(), plugin.getManifest().getVersion().toString(), plugin.getManifest().getAuthors().stream().map(AuthorInfo::getName).toList());
	}

	@Override
	public LoggerWrapper getComponentLogger() {
		return new HytaleLoggerWrapper();
	}

	@Override
	public Collection<WorldHandle> getLoadedWorlds() {
		return universe.getWorlds().values().stream().map(w -> (WorldHandle) new WorldHandleHytale(w)).toList();
	}

	@Override
	public @Nullable WorldHandle getWorld(String worldName) {
		return fromWorld(universe.getWorld(worldName));
	}

	@Override
	public @Nullable WorldHandle getWorld(UUID worldName) {
		return fromWorld(universe.getWorld(worldName));
	}

	@Override
	public @Nullable WorldHandle createWorld(WorldOptions options) {
		return fromWorld(universe.addWorld(options.name()).join());
	}

	@Override
	public boolean unloadWorld(@NotNull WorldHandle world, boolean save) {
		return universe.removeWorld(world.name());
	}

	@Override
	public StackHandle createStack(ItemType type, int amount) {
		return new StackHandleHytale(new ItemStack(type.key().asString(), amount));
	}

	@Override
	public StackHandle createStackFromBytes(byte[] bytes) {
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
	public boolean areAdvancementsDisabled() {
		return true;
	}

	@Override
	public ExtensionTask runTaskTimer(UUID worldId, long repeatTicks, long startDelayTicks, Consumer<ExtensionTask> consumer) {
		return null;
	}

	@Override
	public ExtensionTask runTask(UUID worldId, Consumer<ExtensionTask> consumer) {
		World world = Universe.get().getWorld(worldId);
		if (world == null) {
			ConsoleMessenger.bug("Cannot run plugin task in unknown world (invalid world uuid: " + worldId  + ")", this);
			return null;
		}
		ExtensionTaskHytale wrapper = new ExtensionTaskHytale();
		world.execute(() -> {
			consumer.accept(wrapper);
		});
		return wrapper;
	}

	@Override
	public ExtensionTask runTask(UUID worldId, long startDelayTicks, Consumer<ExtensionTask> consumer) {
		return null;
	}

	@Override
	public void sendConsoleCommand(String command) {

	}

	public static @Nullable WorldHandle fromWorld(@Nullable World world) {
		return world == null ? null : new WorldHandleHytale(world);
	}
}
