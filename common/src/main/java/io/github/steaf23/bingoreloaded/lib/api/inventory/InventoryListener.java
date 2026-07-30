package io.github.steaf23.bingoreloaded.lib.api.inventory;

import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import org.jetbrains.annotations.Nullable;

public interface InventoryListener {
	void itemChanged(InventoryTemplate template, int slot, @Nullable StackHandle newStack, @Nullable StackHandle oldStack);

	void cleared(InventoryTemplate template);

	void contentsReplaced(InventoryTemplate template, StackHandle[] newContents, StackHandle[] oldContents);
}
