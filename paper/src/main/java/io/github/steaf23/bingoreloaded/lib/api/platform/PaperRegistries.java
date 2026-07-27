package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.DimensionType;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.EntityTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.StatisticTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.StatusEffectType;
import io.github.steaf23.bingoreloaded.lib.api.StatusEffectTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Statistic;
import org.jetbrains.annotations.Nullable;

public class PaperRegistries implements PlatformRegistries {

	@Override
	public ItemType resolveItemType(Key key) {
		return new ItemTypePaper(Registry.MATERIAL.get(key));
	}

	@Override
	public @Nullable DimensionType resolveDimensionType(Key key) {
		DimensionType type = null;
		if (key.value().equals("overworld")) {
			if (DimensionType.OVERWORLD == null)
				type = () -> Key.key("minecraft:overworld");
			else
				type = DimensionType.OVERWORLD;
		} else if (key.value().equals("nether")) {
			if (DimensionType.NETHER == null)
				type = () -> Key.key("minecraft:nether");
			else
				type = DimensionType.NETHER;
		} else if (key.value().equals("the_end")) {
			if (DimensionType.THE_END == null)
				type = () -> Key.key("minecraft:the_end");
			else
				type = DimensionType.THE_END;
		}
		return type;
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
	public AdvancementHandle resolveAdvancement(PlatformServer server, Key key) {
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
	public StatusEffectType resolvePotionEffectType(Key key) {
		org.bukkit.potion.PotionEffectType type = Registry.POTION_EFFECT_TYPE.get(key);
		if (type == null) {
			return null;
		}
		return new StatusEffectTypePaper(type);
	}

	@Override
	public StatisticHandle createStatistic(StatisticType type, @Nullable ItemType item, @Nullable EntityType entity) {
		return new StatisticHandlePaper((StatisticTypePaper) type, entity, item);
	}

	@Override
	public boolean areAdvancementsDisabled() {
		return !Bukkit.advancementIterator().hasNext() || Bukkit.advancementIterator().next() == null;
	}
}
