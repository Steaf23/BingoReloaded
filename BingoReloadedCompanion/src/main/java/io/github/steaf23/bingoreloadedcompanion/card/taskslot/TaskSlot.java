package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface TaskSlot {

	Identifier id();

	Item item();

	Component name();

	int completeCount();

	TaskSlot copyWithCount(int newCount);

	default ItemStack createStack() {
		return new ItemStack(item(), completeCount() == 0 ? 1 : completeCount());
	}
}
