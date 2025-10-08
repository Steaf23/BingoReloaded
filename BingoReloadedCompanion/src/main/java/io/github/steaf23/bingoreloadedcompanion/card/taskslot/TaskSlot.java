package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public interface TaskSlot {

	Identifier id();

	Item item();

	Text name();

	int completeCount();

	TaskSlot copyWithCount(int newCount);
}
