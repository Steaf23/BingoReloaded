package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.Menu;
import io.github.steaf23.bingoreloaded.lib.inventory.UserInputMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public class PaperMenus implements PlatformMenus {

	private final PlatformTaskScheduler taskScheduler;

	private final Map<Menu, Inventory> inventories = new IdentityHashMap<>();

	public PaperMenus(PlatformTaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}

	@Override
	public void show(Menu menu, PlayerHandle player) {
		taskScheduler.runTask(task -> {
			Player paperPlayer = ((PlayerHandlePaper) player).handle();

			Inventory inventory = inventories.computeIfAbsent(menu, m -> {
				Inventory inv;
				if (m instanceof UserInputMenu) {
					inv = Bukkit.createInventory(null, InventoryType.ANVIL, m.title());
				} else {
					inv = Bukkit.createInventory(null, m.getBackedInventory().size(), m.title());
				}
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
			if (inventories.get(menu) == inv) {
				return menu;
			}
		}

		return null;
	}
}
