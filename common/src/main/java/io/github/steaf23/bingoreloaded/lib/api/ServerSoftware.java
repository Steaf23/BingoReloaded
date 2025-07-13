package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

//FIXME: REFACTOR Split into several classes (resources, worlds, resolvers, etc..)
//

/**
 * Used by the common code to execute platform specific code.
 */
public interface ServerSoftware {

	InputStream getResource(String filePath);
	void saveResource(String name, boolean replace);
	File getDataFolder();

	Collection<? extends PlayerHandle> getOnlinePlayers();
	@Nullable PlayerHandle getPlayerFromUniqueId(UUID id);
	@Nullable PlayerHandle getPlayerFromName(String name);

	ItemType resolveItemType(Key key);
	DimensionType resolveDimensionType(Key key);
	EntityType resolveEntityType(Key key);

	@Subst("")
	ExtensionInfo getExtensionInfo();

	ComponentLogger getComponentLogger();

	Collection<WorldHandle> getLoadedWorlds();
	@Nullable WorldHandle getWorld(String worldName);
	@Nullable WorldHandle getWorld(UUID worldName);
	@Nullable WorldHandle createWorld(WorldOptions options);
	boolean unloadWorld(@NotNull WorldHandle world, boolean save);

	StackHandle createStack(ItemType type, int amount);
	StackHandle createStackFromBytes(byte[] bytes);
	StackHandle createStackFromTemplate(ItemTemplate template, boolean hideAttributes);
	byte[] createBytesFromStack(StackHandle stack);

	boolean areAdvancementsDisabled();

//	void registerListener(EventListener listener);
//	void unregisterListener(EventListener listener);

	ExtensionTask runTaskTimer(long repeatTicks, long startDelayTicks, Consumer<ExtensionTask> consumer);
	ExtensionTask runTask(Consumer<ExtensionTask> consumer);
	ExtensionTask runTask(long startDelayTicks, Consumer<ExtensionTask> consumer);
}
