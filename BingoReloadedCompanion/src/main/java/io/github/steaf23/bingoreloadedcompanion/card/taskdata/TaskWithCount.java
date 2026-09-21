package io.github.steaf23.bingoreloadedcompanion.card.taskdata;

import io.github.steaf23.bingoreloaded.protocol.data.task.TaskDefinition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public record TaskWithCount(TaskDefinition task, int count, NameSupplier nameSupplier, Set<String> tags) {
	public TaskWithCount copy(int newCount) {
		return new TaskWithCount(task, newCount, nameSupplier, tags);
	}

	public TaskWithCount copy(Set<String> tags) {
		return new TaskWithCount(task, count, nameSupplier, tags);
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
