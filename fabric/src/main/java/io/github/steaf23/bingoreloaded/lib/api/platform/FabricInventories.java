package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;

public class FabricInventories implements PlatformInventories {

	@Override
	public InventoryTemplate playerInventory(PlayerHandle player) {
		return null;
	}

	@Override
	public InventoryTemplate enderChest(PlayerHandle player) {
		return null;
	}

	@Override
	public void addItemToPlayerInventory(PlayerHandle player, StackHandle[] stacks) {

	}
}
