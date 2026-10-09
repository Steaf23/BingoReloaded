package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryListener;
import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleFabric;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FabricInventories implements PlatformInventories {

	@Override
	public InventoryTemplate playerInventory(PlayerHandle player) {
		ServerPlayer fabricPlayer = ((PlayerHandleFabric)player).handle();
		return wrapContainer(fabricPlayer.getInventory());
	}

	@Override
	public InventoryTemplate enderChest(PlayerHandle player) {
		ServerPlayer fabricPlayer = ((PlayerHandleFabric)player).handle();
		return wrapContainer(fabricPlayer.getEnderChestInventory());
	}

	@Override
	public List<StackHandle> addItemToPlayerInventory(PlayerHandle player, StackHandle[] stacks) {
		List<StackHandle> result = new ArrayList<>();
		ServerPlayer fabricPlayer = player(player);
		for (StackHandle stack : stacks) {
			fabricPlayer.addItem(((StackHandleFabric)stack).handle());
			if (stack.amount() != 0) {
				result.add(stack);
			}
		}

		return result;
	}

	private ServerPlayer player(PlayerHandle player) {
		return ((PlayerHandleFabric)player).handle();
	}

	InventoryTemplate wrapContainer(Container container) {
		ContainerUpdater updater = new ContainerUpdater(container);

		StackHandle[] handles = new StackHandle[container.getContainerSize()];
		for (int i = 0; i < handles.length; i++) {
			handles[i] = new StackHandleFabric(container.getItem(i));
		}
		InventoryTemplate template = new InventoryTemplate(handles);

		template.addListener(updater);
		return template;
	}

	public record ContainerUpdater(Container inventory) implements InventoryListener {

		@Override
		public void itemChanged(InventoryTemplate template, int slot, @Nullable StackHandle newStack, @Nullable StackHandle oldStack) {
			this.inventory.setItem(slot, newStack == null ? null : ((StackHandleFabric) newStack).handle());
		}

		@Override
		public void cleared(InventoryTemplate template) {
			this.inventory.clearContent();
		}

		@Override
		public void contentsReplaced(InventoryTemplate template, StackHandle[] newContents, StackHandle[] oldContents) {
			for (int slot = 0; slot < newContents.length; slot++) {
				this.inventory.setItem(slot, ((StackHandleFabric) newContents[slot]).handle());
			}
		}
	}

}
