package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;

public record StatisticTypeTask<T>(StatType<T> stat, T data, int count) implements TaskSlot {

	@Override
	public Identifier id() {
		return BuiltInRegistries.STAT_TYPE.getKey(stat);
	}

	@Override
	public Item item() {
		return switch (data) {
			case Item itemData -> itemData;
			case EntityType<?> entity -> SpawnEggItem.byId(entity);
			case Block blockData -> blockData.asItem();
			default -> Items.BEDROCK;
		};
	}

	@Override
	public Component name() {
		return stat.getDisplayName();
	}

	@Override
	public int completeCount() {
		return count;
	}

	@Override
	public TaskSlot copyWithCount(int newCount) {
		return new StatisticTypeTask<>(stat, data, newCount);
	}
}
