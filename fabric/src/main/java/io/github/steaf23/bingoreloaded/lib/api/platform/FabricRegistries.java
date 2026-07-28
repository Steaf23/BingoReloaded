package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.DimensionType;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.EntityTypeFabric;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.StatisticTypeFabric;
import io.github.steaf23.bingoreloaded.lib.api.StatusEffectType;
import io.github.steaf23.bingoreloaded.lib.api.StatusEffectTypeFabric;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypeFabric;
import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityTypes;
import org.jetbrains.annotations.Nullable;

public class FabricRegistries implements PlatformRegistries {

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
	public AdvancementHandle resolveAdvancement(PlatformServer server, Key key) {
		return new AdvancementHandleFabric(FabricTypes.idFromKey(key), ((FabricServer)server).handle());
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
		Holder<MobEffect> holder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(type);
		return new StatusEffectTypeFabric(holder);
	}

	@Override
	public StatisticHandle createStatistic(StatisticType type, @Nullable ItemType item, @Nullable EntityType entity) {
		return null;
	}

	@Override
	public boolean areAdvancementsDisabled() {
		return false;
	}
}
