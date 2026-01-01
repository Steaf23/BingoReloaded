package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public interface TaskSlot {

	Identifier id();

	Item item();

	Component name();

	int completeCount();

	TaskSlot copyWithCount(int newCount);
}
