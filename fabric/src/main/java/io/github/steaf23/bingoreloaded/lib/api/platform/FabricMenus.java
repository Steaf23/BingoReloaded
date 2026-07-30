package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.item.StackHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleFabric;
import io.github.steaf23.bingoreloaded.lib.inventory.Menu;
import net.minecraft.client.Minecraft;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class FabricMenus implements PlatformMenus {

	@Override
	public void show(Menu menu, PlayerHandle player) {
		menu.getBackedInventory();

		SimpleContainer container = new SimpleContainer(Arrays.stream(menu.getBackedInventory().contents())
				.map(s -> ((StackHandleFabric)s).handle())
				.toArray(ItemStack[]::new));
		((PlayerHandleFabric)player).handle().openMenu(new SimpleMenuProvider((id, playerInv, p) -> {
			return new AbstractContainerMenu(MenuType.GENERIC_9x3, id) {
				@Override
				public ItemStack quickMoveStack(Player player, int slotIndex) {
					return null;
				}

				@Override
				public boolean stillValid(Player player) {
					return false;
				}
			};
		}, Minecraft.append(menu.title())));
	}

	@Override
	public void close(PlayerHandle playerHandle) {

	}

	@Override
	public void remove(Menu menu, PlayerHandle playerHandle) {

	}
}
