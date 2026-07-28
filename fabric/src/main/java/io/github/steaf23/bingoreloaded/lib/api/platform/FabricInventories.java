package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandle;
import net.kyori.adventure.text.Component;

public class FabricInventories implements PlatformInventories {

	@Override
	public InventoryHandle createInventory(int slots, Component title) {
		return null;
	}

	@Override
	public InventoryHandle createInventory(Type type, Component title) {
		return null;
	}
}
