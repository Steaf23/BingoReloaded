package io.github.steaf23.bingoreloaded.lib.api.player;

import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInventoryHandlePaper extends InventoryHandlePaper implements PlayerInventoryHandle {

	private final PlayerInventory inventory;

	public PlayerInventoryHandlePaper(PlayerInventory inventory) {
		super(inventory);
		this.inventory = inventory;
	}

	@Override
	public StackHandle mainHandItem() {
		return new StackHandlePaper(inventory.getItemInMainHand());
	}

	@Override
	public StackHandle offHandItem() {
		return new StackHandlePaper(inventory.getItemInOffHand());
	}

	public PlayerInventory handle() {
		return inventory;
	}
}
