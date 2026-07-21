package io.github.steaf23.bingoreloaded.lib.api.item;

import net.kyori.adventure.text.Component;

public interface CapacityInventoryProvider {

	void setSlotCount(int slots);
	void setTitle(Component title);

	InventoryHandle create();
}
