package io.github.steaf23.bingoreloaded.lib.api;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class InventoryHandlePaper implements InventoryHandle {

	private final Inventory inventory;

	public InventoryHandlePaper(Inventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public void setItem(int index, StackHandle stack) {
		inventory.setItem(index, ((StackHandlePaper)stack).handle());
	}

	@Override
	public void addItem(StackHandle... stacks) {
		inventory.addItem(Arrays.stream(stacks).map(s -> ((StackHandlePaper)s).handle()).toArray(ItemStack[]::new));
	}

	@Override
	public StackHandle[] contents() {
		return Arrays.stream(inventory.getContents()).map(StackHandlePaper::new).toArray(StackHandle[]::new);
	}

	@Override
	public void clearContents() {
		inventory.clear();
	}

	@Override
	public void setContents(StackHandle[] contents) {
		inventory.setContents(Arrays.stream(contents).map(s -> ((StackHandlePaper)s).handle()).toArray(ItemStack[]::new));
	}
}
