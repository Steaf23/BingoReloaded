package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
	public Text name() {
		return Text.translatable(Registries.CUSTOM_STAT.get(stat).toTranslationKey());
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
