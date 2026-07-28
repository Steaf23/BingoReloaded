package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandle;
import net.kyori.adventure.text.Component;

public interface PlatformInventories {

	enum Type {
		GENERIC,
		ANVIL,
	}

	InventoryHandle createInventory(int slots, Component title);
	InventoryHandle createInventory(Type type, Component title);
}
