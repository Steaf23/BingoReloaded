package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.lib.api.DimensionType;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.PlatformBridge;
import io.github.steaf23.bingoreloaded.lib.api.ExtensionTask;
import io.github.steaf23.bingoreloaded.lib.api.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldOptions;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class FabricPlatformBridge implements PlatformBridge {

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
	public void getExtensionInfo() {

	}

	@Override
	public ComponentLogger getComponentLogger() {
		return null;
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
	public ExtensionTask runTaskTimer(long repeatTicks, long startDelay, Runnable runnable) {
		return null;
	}
}
