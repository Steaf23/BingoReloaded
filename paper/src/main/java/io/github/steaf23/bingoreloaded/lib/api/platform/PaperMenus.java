package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public class PaperMenus implements PlatformMenus {

	private final PlatformTaskScheduler taskScheduler;

	private final Map<Menu, Inventory> inventories = new IdentityHashMap<>();

	public PaperMenus(PlatformTaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}

	public static class MenuHolder implements InventoryHolder {

		private Inventory inventory;
		private final Menu menu;

		public MenuHolder(Menu menu) {
			this.menu = menu;
		}

		public void setInventory(Inventory inventory) {
			this.inventory = inventory;
		}

		@Override
		public @NotNull Inventory getInventory() {
			return inventory;
		}
	}

	@Override
	public void show(Menu menu, PlayerHandle player) {
		taskScheduler.runTask(task -> {
			Player paperPlayer = ((PlayerHandlePaper) player).handle();

			Inventory inventory = inventories.computeIfAbsent(menu, m -> {
				MenuHolder holder = new MenuHolder(menu);
				Inventory inv = switch (m.type()) {
					case CHEST -> Bukkit.createInventory(holder, m.getBackedInventory().size(), m.title());
					case ANVIL -> Bukkit.createInventory(holder, InventoryType.ANVIL, m.title());
					case DROPPER -> Bukkit.createInventory(holder, InventoryType.DROPPER, m.title());
				};
				holder.setInventory(inv);
				sync(inv, m.getBackedInventory());
				m.getBackedInventory().addListener(new PaperInventories.BukkitInventoryUpdater(inv));
				return inv;
			});

			paperPlayer.openInventory(inventory);
		});
	}

	private void sync(Inventory inventory, InventoryTemplate template) {
		for (int i = 0; i < template.size(); i++) {
			inventory.setItem(i, ((StackHandlePaper) template.getItem(i)).handle());
		}
	}

	@Override
	public void close(PlayerHandle player) {
		taskScheduler.runTask(task -> {
			player.closeInventory();
		});
	}

	@Override
	public void remove(Menu menu, PlayerHandle playerHandle) {
		inventories.remove(menu);
	}

	public @Nullable Menu menuFor(Inventory inv) {
		for (Menu menu : inventories.keySet()) {
			if (Objects.equals(inventories.get(menu).getHolder(), inv.getHolder())) {
				return menu;
			}
		}

		return null;
	}
}
