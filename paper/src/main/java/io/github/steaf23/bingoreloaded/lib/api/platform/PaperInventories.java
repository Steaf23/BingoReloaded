package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandlePaper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;

public class PaperInventories implements PlatformInventories {

	@Override
	public InventoryHandle createInventory(int slots, Component title) {
		return new InventoryHandlePaper(Bukkit.createInventory(null, slots, title));
	}

	@Override
	public InventoryHandle createInventory(Type type, Component title) {
		return new InventoryHandlePaper(Bukkit.createInventory(null, type == Type.ANVIL ? InventoryType.ANVIL : InventoryType.CHEST));
	}
}
