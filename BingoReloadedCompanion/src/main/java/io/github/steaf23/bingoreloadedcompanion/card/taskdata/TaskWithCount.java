package io.github.steaf23.bingoreloadedcompanion.card.taskdata;

import io.github.steaf23.bingoreloaded.protocol.data.task.TaskDefinition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record TaskWithCount(TaskDefinition task, int count, NameSupplier nameSupplier) {
	public TaskWithCount copy(int newCount) {
		return new TaskWithCount(task, newCount, nameSupplier);
	}

	public boolean isSelected() {
		return count != 0;
	}

	public ItemStack createStack() {
		return new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(task.iconItem().namespace(), task.iconItem().value())));
	}

	public String countString() {
		return count == 0 ? null : "" + count;
	}

	public Component getName() {
		return nameSupplier.get(this);
	}

	@FunctionalInterface
	public interface NameSupplier {
		Component get(TaskWithCount task);
	}
}
