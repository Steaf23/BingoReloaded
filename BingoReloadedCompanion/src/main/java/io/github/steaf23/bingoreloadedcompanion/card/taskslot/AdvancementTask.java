package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record AdvancementTask(AdvancementHolder advancement, boolean counted) implements TaskSlot {

	@Override
	public Identifier id() {
		return advancement.id();
	}

	@Override
	public Item item() {
		if (advancement.value().display().isPresent()) {
			return advancement.value().display().get().getIcon().getItem();
		}
		return Items.BEDROCK;
	}

	@Override
	public Component name() {
		return advancement.value().name().orElse(Component.nullToEmpty("UNNAMED ADVANCEMENT"));
	}

	@Override
	public int completeCount() {
		return counted ? 1 : 0;
	}

	@Override
	public TaskSlot copyWithCount(int newCount) {
		return new AdvancementTask(advancement, newCount > 0);
	}
}
