package io.github.steaf23.bingoreloaded.lib.api.player;

import com.hypixel.hytale.server.core.inventory.Inventory;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandleHytale;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandleHytale;

public class PlayerInventoryHandleHytale extends InventoryHandleHytale implements PlayerInventoryHandle {

	public PlayerInventoryHandleHytale(Inventory inventory) {
		super(inventory);
	}

	@Override
	public StackHandle mainHandItem() {
		return new StackHandleHytale(handle().getItemInHand());
	}

	@Override
	public StackHandle offHandItem() {
		return new StackHandleHytale(handle().getUtilityItem());
	}
}
