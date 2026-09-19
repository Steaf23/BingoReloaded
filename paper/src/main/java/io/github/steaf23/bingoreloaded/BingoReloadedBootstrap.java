package io.github.steaf23.bingoreloaded;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.PreFlattenTagRegistrar;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.block.BlockType;

import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class BingoReloadedBootstrap implements PluginBootstrap {

	public static final TagKey<BlockType> ORES = BlockTypeTagKeys.create(Key.key("minecraft:ores"));

	@Override
	public void bootstrap(BootstrapContext context) {
		final LifecycleEventManager<BootstrapContext> manager = context.getLifecycleManager();
		manager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.BLOCK), event -> {
			final PreFlattenTagRegistrar<BlockType> registrar = event.registrar();
			registrar.setTag(ORES, Set.of(
					TagEntry.tagEntry(BlockTypeTagKeys.COPPER_ORES),
					TagEntry.tagEntry(BlockTypeTagKeys.GOLD_ORES),
					TagEntry.tagEntry(BlockTypeTagKeys.IRON_ORES),
					TagEntry.tagEntry(BlockTypeTagKeys.COAL_ORES),
					TagEntry.tagEntry(BlockTypeTagKeys.DIAMOND_ORES),
					TagEntry.tagEntry(BlockTypeTagKeys.EMERALD_ORES),
					TagEntry.tagEntry(BlockTypeTagKeys.LAPIS_ORES),
					TagEntry.tagEntry(BlockTypeTagKeys.REDSTONE_ORES),
					TagEntry.valueEntry(TypedKey.create(RegistryKey.BLOCK, Material.NETHER_QUARTZ_ORE.getKey()))
			));
		});
	}
}
