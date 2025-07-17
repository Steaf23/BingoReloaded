package io.github.steaf23.bingoreloaded.lib.api.player;

import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandle;

public interface PlayerInventoryHandle extends InventoryHandle {

	StackHandle mainHandItem();
	StackHandle offHandItem();
}
