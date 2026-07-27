package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.kyori.adventure.key.Key;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.StatType;
import org.jetbrains.annotations.NotNull;

public class StatisticTypeFabric implements StatisticType {

	private final StatType<?> type;

	public StatisticTypeFabric(@NotNull StatType<?> type) {
		this.type = type;
	}

	public StatType<?> handle() {
		return type;
	}

	// FIXME: FABRIC REFACTOR
	@Override
	public StatisticCategory getCategory() {
		return null;
	}

	@Override
	public @NotNull Key key() {
		return FabricTypes.keyFromId(BuiltInRegistries.STAT_TYPE.getKey(type));
	}
}
