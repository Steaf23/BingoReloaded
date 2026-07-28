package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.DimensionType;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.StatusEffectType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import net.kyori.adventure.key.Key;

import java.util.List;

public interface PlatformRegistries {

	ItemType resolveItemType(Key key);
	DimensionType resolveDimensionType(Key key);
	EntityType resolveEntityType(Key key);
	AdvancementHandle resolveAdvancement(PlatformServer server, Key key);
	StatusEffectType resolvePotionEffectType(Key key);

	boolean areAdvancementsDisabled();

	List<ItemType> allItems();
}
