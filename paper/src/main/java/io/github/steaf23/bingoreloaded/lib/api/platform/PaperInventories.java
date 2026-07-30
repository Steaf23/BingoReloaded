package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryListener;
import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PaperInventories implements PlatformInventories {


	@Override
	public InventoryTemplate playerInventory(PlayerHandle player) {
		return wrapBukkitInventory(((PlayerHandlePaper)player).handle().getInventory());
	}

	@Override
	public InventoryTemplate enderChest(PlayerHandle player) {
		return wrapBukkitInventory(((PlayerHandlePaper)player).handle().getEnderChest());
	}

	@Override
	public void addItemToPlayerInventory(PlayerHandle player, StackHandle[] stacks) {
		((PlayerHandlePaper)player).handle().getInventory().addItem(mapStackArray(stacks));
	}

	InventoryTemplate wrapBukkitInventory(Inventory bukkitInv) {
		BukkitInventoryUpdater updater = new BukkitInventoryUpdater(bukkitInv);

		StackHandle[] handles = new StackHandle[bukkitInv.getSize()];
		for (int i = 0; i < handles.length; i++) {
			handles[i] = new StackHandlePaper(bukkitInv.getItem(i));
		}
		InventoryTemplate template = new InventoryTemplate(handles);

		template.addListener(updater);
		return template;
	}

	public record BukkitInventoryUpdater(Inventory inventory) implements InventoryListener {

		@Override
		public void itemChanged(InventoryTemplate template, int slot, @Nullable StackHandle newStack, @Nullable StackHandle oldStack) {
			this.inventory.setItem(slot, newStack == null ? null : ((StackHandlePaper) newStack).handle());
		}

		@Override
		public void cleared(InventoryTemplate template) {
			this.inventory.clear();
		}

		@Override
		public void contentsReplaced(InventoryTemplate template, StackHandle[] newContents, StackHandle[] oldContents) {
			this.inventory.setContents(mapStackArray(newContents));
		}
	}

	private static ItemStack[] mapStackArray(StackHandle[] handles) {
		return Arrays.stream(handles)
				.map(s -> ((StackHandlePaper)s).handle())
				.toArray(ItemStack[]::new);
	}
}
