package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.item.TeamPouch;
import io.github.steaf23.bingoreloaded.lib.api.item.CapacityInventoryProvider;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PaperInventoryProvider implements CapacityInventoryProvider, Listener {

	static class DummyHolder implements InventoryHolder {

		private Inventory inventory;

		@Override
		public @NotNull Inventory getInventory() {
			return inventory;
		}
	}

	private int slots = 54;
	private Component title = Component.text("Inventory");

	public PaperInventoryProvider(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void setSlotCount(int slots) {
		this.slots = slots;
	}

	@Override
	public void setTitle(Component title) {
		this.title = title;
	}

	@Override
	public InventoryHandle create() {
		DummyHolder holder = new DummyHolder();
		int startRows = slots / 9;
		int extraSlots = slots % 9;
		int rows = extraSlots > 0 ? startRows + 1 : startRows;
		holder.inventory = Bukkit.createInventory(holder, rows * 9, title);

		InventoryHandle handle = new InventoryHandlePaper(holder.inventory);
		if (rows == startRows) {
			return handle;
		}

		for (int i = 0; i < 9 - extraSlots; i++) {
			ItemTemplate item = BasicMenu.BLANK.copyToSlot(8 - i, rows - 1).setCompareKey("locked");
			handle.setItem(item.getSlot(), item.buildItem());
		}
		return handle;
	}

	@EventHandler
	public void inventoryClick(final InventoryClickEvent event) {
		// Check if this inventory should be checked by the provider
		if (!(event.getInventory().getHolder() instanceof DummyHolder)) {
			return;
		}

		StackHandle stack = new StackHandlePaper(event.getCurrentItem());

		if (ItemTemplate.isCompareKeyEqual(stack, "locked")) {
			event.setCancelled(true);
			return;
		}

		if (ItemTemplate.isCompareKeyEqual(stack, TeamPouch.ID.asString())
		|| ItemTemplate.isCompareKeyEqual(stack, PlayerKit.CARD_ITEM.getCompareKey())) {
			event.setCancelled(true);
		}
	}
}
