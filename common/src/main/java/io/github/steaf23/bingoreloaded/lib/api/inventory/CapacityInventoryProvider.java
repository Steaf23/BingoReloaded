package io.github.steaf23.bingoreloaded.lib.api.inventory;

import net.kyori.adventure.text.Component;

public interface CapacityInventoryProvider {

	void setSlotCount(int slots);
	void setTitle(Component title);

	InventoryTemplate create();
}
