package io.github.steaf23.bingoreloaded.lib.api;

import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;

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
	public HashMap<Integer, StackHandle> addItem(StackHandle... stacks) {
		var result = inventory.addItem(Arrays.stream(stacks).map(s -> ((StackHandlePaper)s).handle()).toArray(ItemStack[]::new));

		HashMap<Integer, StackHandle> returnedHandles = new HashMap<>();
		for (Integer idx : result.keySet()) {
			returnedHandles.put(idx, new StackHandlePaper(result.get(idx)));
		}

		return returnedHandles;
	}

	@Override
	public @NotNull StackHandle getItem(int index) {
		ItemStack stack = inventory.getItem(index);
		if (stack == null) {
			return StackHandle.create(ItemType.AIR);
		}
		return new StackHandlePaper(stack);
	}

	@Override
	public void removeItem(StackHandle stack) {
		inventory.remove(((StackHandlePaper)stack).handle());
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
