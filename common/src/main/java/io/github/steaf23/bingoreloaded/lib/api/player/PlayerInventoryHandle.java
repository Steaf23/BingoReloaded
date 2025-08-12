package io.github.steaf23.bingoreloaded.lib.api.player;

import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;

public interface PlayerInventoryHandle extends InventoryHandle {

	StackHandle mainHandItem();
	StackHandle offHandItem();
}
