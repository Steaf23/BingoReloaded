package io.github.steaf23.bingoreloaded.lib.inventory;

import io.github.steaf23.bingoreloaded.lib.api.PlayerHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.platform.PaperMenus;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.events.PlayerDisplayAnvilTextChangedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InventoryEventListener implements Listener {

	private final MenuBoard board;
	private final PaperMenus menus;

	public InventoryEventListener(MenuBoard board, PaperMenus menus) {
		this.board = board;
		this.menus = menus;
	}

	@EventHandler
	public void handleInventoryClick(final InventoryClickEvent event) {
		ClickType type = switch(event.getClick()) {
			case LEFT -> ClickType.LEFT_CLICK;
			case RIGHT -> ClickType.RIGHT_CLICK;
			case SHIFT_LEFT -> ClickType.SHIFT_LEFT;
			case SHIFT_RIGHT -> ClickType.SHIFT_RIGHT;
			case WINDOW_BORDER_LEFT -> ClickType.WINDOW_BORDER_LEFT;
			case WINDOW_BORDER_RIGHT -> ClickType.WINDOW_BORDER_RIGHT;
			case MIDDLE -> ClickType.MIDDLE;
			case NUMBER_KEY -> ClickType.NUMBER_KEY;
			case DOUBLE_CLICK -> ClickType.DOUBLE_CLICK;
			case DROP -> ClickType.DROP;
			case CONTROL_DROP -> ClickType.CONTROL_DROP;
			case CREATIVE -> ClickType.CREATIVE;
			case SWAP_OFFHAND -> ClickType.SWAP_OFFHAND;
			case UNKNOWN -> ClickType.UNKNOWN;
		};

		if (board.inventoryClicked(
				new PlayerHandlePaper(board.context().server(), (Player)event.getWhoClicked()),
				menus.menuFor(event.getInventory()),
				type,
				event.getRawSlot(),
				event.getCurrentItem() == null ? null : new StackHandlePaper(event.getCurrentItem())) == EventResult.CONSUME) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handleInventoryDrag(final InventoryDragEvent event) {
		if (board.inventoryDragged(new PlayerHandlePaper(board.context().server(), (Player)event.getWhoClicked()), menus.menuFor(event.getInventory())) == EventResult.CONSUME) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handleInventoryClose(final InventoryCloseEvent event) {
		board.inventoryClosed(new PlayerHandlePaper(board.context().server(), (Player) event.getPlayer()), menus.menuFor(event.getInventory()));
	}

	@EventHandler
	public void handlePlayerQuit(final PlayerQuitEvent event) {
		board.playerQuit(new PlayerHandlePaper(board.context().server(), event.getPlayer()));
	}

	@EventHandler
	public void handlePlayerDisplayAnvilTextChanged(final PlayerDisplayAnvilTextChangedEvent event) {
		board.anvilTextChanged(event.getUserId(), event.getNewText());
	}
}
