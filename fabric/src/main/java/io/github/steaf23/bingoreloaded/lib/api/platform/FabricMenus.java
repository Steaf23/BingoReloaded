package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleFabric;
import io.github.steaf23.bingoreloaded.lib.inventory.FabricMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.Menu;
import io.github.steaf23.bingoreloaded.mixin.ServerPlayerAccessor;
import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;

public class FabricMenus implements PlatformMenus {

	private final PlatformTaskScheduler taskScheduler;

	public FabricMenus(PlatformTaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}

	@Override
	public void show(Menu menu, PlayerHandle player) {

		ServerPlayer serverPlayer = ((PlayerHandleFabric)player).handle();

		((ServerPlayerAccessor) serverPlayer).bingoreloaded$nextContainerCounter();
		int containerId = ((ServerPlayerAccessor) serverPlayer).bingoreloaded$containerCounter();

		MenuType<?> menuType = getMenuTypeForContainerMenu(menu.type(), menu.getBackedInventory().size());

		FabricMenu fabricMenu = new FabricMenu(player,
				menuType,
				containerId,
				menu,
				serverPlayer.getInventory()
		);
		Component title = FabricTypes.toNativeComponent(player.server(), menu.title());

		if (!isPlayerImmobile(serverPlayer)) {
			serverPlayer.connection.send(new ClientboundOpenScreenPacket(containerId, menuType, title));
		}
		serverPlayer.containerMenu = fabricMenu;
		((ServerPlayerAccessor) serverPlayer).bingoreloaded$initMenu(fabricMenu);
	}

	private boolean isPlayerImmobile(ServerPlayer player) {
		return player.isDeadOrDying() || player.isSleeping() || player.isRemoved();
	}

	@Override
	public void close(PlayerHandle playerHandle) {
		playerHandle.closeInventory();
	}

	@Override
	public void remove(Menu menu, PlayerHandle playerHandle) {
		// not needed in fabric
	}

	public MenuType<?> getMenuTypeForContainerMenu(io.github.steaf23.bingoreloaded.lib.inventory.MenuType type, int slots) {
		return switch (type) {
			case CHEST -> {
				int rows = (int)Math.ceil(slots / 9.0D);
				yield switch (rows) {
					case 1 -> MenuType.GENERIC_9x1;
					case 2 -> MenuType.GENERIC_9x2;
					case 3 -> MenuType.GENERIC_9x3;
					case 4 -> MenuType.GENERIC_9x4;
					case 5 -> MenuType.GENERIC_9x5;
					default -> MenuType.GENERIC_9x6;
				};
			}
			case ANVIL -> MenuType.ANVIL;
		};
	}
}
