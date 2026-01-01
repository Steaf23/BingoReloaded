package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.Item;

public record ItemTask(Identifier id, int count) implements TaskSlot {

	public ItemTask(Identifier id, int count) {
		this.id = id;
		this.count = Math.clamp(count, 0, 64);
	}

	public Item item() {
		return BuiltInRegistries.ITEM.getValue(id);
	}

	public Component name() {
		return Component.nullToEmpty(count + "x ").copy().withColor(CommonColors.SOFT_YELLOW)
				.append(Component.translatable(item().getDescriptionId()));
	}

	@Override
	public int completeCount() {
		return 0;
	}

	@Override
	public ItemTask copyWithCount(int newCount) {
		return new ItemTask(id, newCount);
	}
}
