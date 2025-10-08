package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.stat.StatType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record StatisticTypeTask<T>(StatType<T> stat, T data, int count) implements TaskSlot {

	@Override
	public Identifier id() {
		return Registries.STAT_TYPE.getId(stat);
	}

	@Override
	public Item item() {
		return switch (data) {
			case Item itemData -> itemData;
			case EntityType<?> entity -> SpawnEggItem.forEntity(entity);
			case Block blockData -> blockData.asItem();
			default -> Items.BEDROCK;
		};
	}

	@Override
	public Text name() {
		return stat.getName();
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
