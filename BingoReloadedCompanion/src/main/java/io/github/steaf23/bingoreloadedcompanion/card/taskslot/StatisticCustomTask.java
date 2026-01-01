package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record StatisticCustomTask(Identifier stat, int count) implements TaskSlot {

	@Override
	public Identifier id() {
		return stat;
	}

	@Override
	public Item item() {
		return Items.DRIED_GHAST;
	}

	@Override
	public Component name() {
		return Component.translatable(BuiltInRegistries.CUSTOM_STAT.getValue(stat).toLanguageKey());
	}

	@Override
	public int completeCount() {
		return count;
	}

	@Override
	public TaskSlot copyWithCount(int newCount) {
		return new StatisticCustomTask(stat, newCount);
	}
}
