package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public record TaskWithCount(TaskDefinition task, int count) {
	public TaskWithCount copy(int newCount) {
		return new TaskWithCount(task, newCount);
	}

	public boolean isSelected() {
		return count != 0;
	}

	public ItemStack createStack() {
		return new ItemStack(BuiltInRegistries.ITEM.getValue(task.iconItem()));
	}

	public String countString() {
		return count == 0 ? null : "" + count;
	}
}
