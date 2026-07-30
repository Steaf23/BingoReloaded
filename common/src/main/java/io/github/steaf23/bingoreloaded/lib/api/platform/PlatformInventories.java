package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;

public interface PlatformInventories {

	/**
	 * Returns a wrapper around the player's current player inventory contents.

	 * Changes made through the returned InventoryTemplate are propagated
	 * to the internal inventory. External changes made after this method
	 * returns are not reflected in the template.
	 */
	InventoryTemplate playerInventory(PlayerHandle player);

	/**
	 * Returns a wrapper around the player's current ender chest contents.

	 * Changes made through the returned InventoryTemplate are propagated
	 * to the internal inventory. External changes made after this method
	 * returns are not reflected in the template.
	 */
	InventoryTemplate enderChest(PlayerHandle player);

	void addItemToPlayerInventory(PlayerHandle player, StackHandle[] stacks);
}
