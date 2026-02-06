package io.github.steaf23.bingoreloaded.lib.api.item;

import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InventoryHandleHytale implements InventoryHandle {


	Inventory inventory;

	public InventoryHandleHytale(Inventory inventory) {
		this.inventory = inventory;
	}

	public Inventory handle() {
		return inventory;
	}

	@Override
	public void setItem(int index, StackHandle stack) {
		inventory.getCombinedHotbarUtilityConsumableStorage().setItemStackForSlot((short) index, ((StackHandleHytale)stack).handle());
	}

	@Override
	public HashMap<Integer, StackHandle> addItem(StackHandle... stacks) {
		ListTransaction<ItemStackTransaction> transaction = inventory.getCombinedHotbarUtilityConsumableStorage()
				.addItemStacks(Arrays.stream(stacks).map(handle -> ((StackHandleHytale)handle).stack).toList());
		HashMap<Integer, StackHandle> remainder = new HashMap<>();

		int idx = 0;
		for (ItemStackTransaction t : transaction.getList()) {
			remainder.put(idx, new StackHandleHytale(t.getRemainder()));

			idx += 1;
		}

		return remainder;
	}

	@Override
	public @NotNull StackHandle getItem(int index) {
		return new StackHandleHytale(inventory.getCombinedHotbarUtilityConsumableStorage().getItemStack((short) index));
	}

	@Override
	public void removeItem(StackHandle stack) {
		inventory.getCombinedHotbarUtilityConsumableStorage().removeItemStack(((StackHandleHytale)stack).stack);
	}

	@Override
	public StackHandle[] contents() {
		List<StackHandle> stacks = new ArrayList<>();
		CombinedItemContainer container = inventory.getCombinedHotbarUtilityConsumableStorage();
		for (short slot = 0; slot < container.getCapacity(); slot++) {
			ItemStack stack = container.getItemStack(slot);
			stacks.add(stack == null ?  null : new StackHandleHytale(stack));
		}
		return stacks.toArray(StackHandle[]::new);
	}

	@Override
	public void clearContents() {
		inventory.getCombinedHotbarUtilityConsumableStorage().removeAllItemStacks();
	}

	@Override
	public void setContents(StackHandle[] contents) {
		ItemContainer container = inventory.getCombinedHotbarUtilityConsumableStorage();

		for (StackHandle s : contents) {

		}
	}
}
