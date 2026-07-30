package io.github.steaf23.bingoreloaded.lib.inventory;


import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.platform.GameContext;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MenuBoard {

	private static final Set<ClickType> CLICK_TYPES_TO_IGNORE = Set.of(ClickType.DOUBLE_CLICK, ClickType.DROP, ClickType.CREATIVE, ClickType.CONTROL_DROP, ClickType.SWAP_OFFHAND);

	private final GameContext context;
	// Stores all currently open inventories by all players, using a stack system we can easily add or remove child inventories.
	protected final Map<UUID, MenuStack> activeMenus;

	public MenuBoard(GameContext context) {
		this.context = context;
		this.activeMenus = new HashMap<>();
	}

	public void open(Menu menu, PlayerHandle player) {
		MenuStack stack = getOrCreateStack(player);

		MenuStack.PushResult result = stack.push(menu);
		if (result.menuToClose() != null) {
			result.menuToClose().beforeClosing(player);
		}
		show(menu, player);
	}

	public void close(Menu menu, PlayerHandle player) {
		UUID playerId = player.uniqueId();
		if (!activeMenus.containsKey(playerId))
			return;

		// Return early if it's not on top of the menu stack (anymore).
		// This also guards against infinite closing loops regarding the closeEvent
		MenuStack stack = activeMenus.get(playerId);
		Menu next = stack.pop(menu);
		if (next == menu) {
			return;
		}
		context.menus().remove(menu, player);
		menu.beforeClosing(player);

		if (next == null) {
			activeMenus.remove(playerId);
			context.menus().close(player);
		} else {
			show(next, player);
		}
	}

	public void closeAll(PlayerHandle player) {
		UUID playerId = player.uniqueId();
		if (!activeMenus.containsKey(playerId))
			return;

		MenuStack menus = activeMenus.get(playerId);
		menus.popAll(m -> {
			context.menus().remove(m, player);
			m.beforeClosing(player);
		});
		activeMenus.remove(playerId);
		context.menus().close(player);
	}

	public GameContext context() {
		return context;
	}

	protected EventResult<?> inventoryClicked(PlayerHandle player, Menu menu, ClickType clickType, int rawSlot, StackHandle clickedItem) {
		Menu current = currentMenu(player);
		if (current == null || current != menu) {
			return EventResult.IGNORE;
		}

		// ignore click types that break everything
		if (CLICK_TYPES_TO_IGNORE.contains(clickType))
			return EventResult.CONSUME;

		if (current.getBackedInventory().size() < rawSlot
				|| rawSlot < 0 || clickedItem == null || clickedItem.type().isAir())
			return EventResult.CONSUME;

		boolean cancel = current.onClick(
				player,
				rawSlot,
				clickType);
		return cancel ? EventResult.CONSUME : EventResult.IGNORE;
	}

	protected EventResult<?> inventoryDragged(PlayerHandle player, Menu menu) {
		Menu current = currentMenu(player);
		if (current == null || current != menu) {
			return EventResult.IGNORE;
		}

		boolean cancel = current.onDrag();
		return cancel ? EventResult.CONSUME : EventResult.IGNORE;
	}

	protected void inventoryClosed(PlayerHandle player, Menu menu) {
		Menu current = currentMenu(player);
		if (current == null || current != menu) {
			return;
		}

		close(current, player);
	}

	protected void playerQuit(PlayerHandle player) {
		if (activeMenus.containsKey(player.uniqueId())) {
			closeAll(player);
		}
	}

	protected void anvilTextChanged(UUID playerId, String newText) {
		if (!activeMenus.containsKey(playerId))
			return;

		// There is no direct reference to the actual inventory in this event,
		// which is fine because a player can only open a single inventory at a time.
		// We just have to check the currently opened inventory for the given user uuid.
		MenuStack stack = activeMenus.get(playerId);
		if (stack == null) {
			return;
		}
		Menu topMenu = stack.peek();

		if (topMenu instanceof UserInputMenu inputMenu) {
			inputMenu.handleTextChanged(newText);
		}
	}

	private MenuStack getOrCreateStack(PlayerHandle player) {
		return activeMenus.computeIfAbsent(player.uniqueId(), id -> new MenuStack());
	}

	private @Nullable Menu currentMenu(PlayerHandle player) {
		UUID id = player.uniqueId();
		MenuStack stack = activeMenus.getOrDefault(id, null);
		return stack == null ? null : stack.peek();
	}

	private void show(Menu menu, PlayerHandle player) {
		menu.beforeOpening(player);
		context.menus().show(menu, player);
	}
}
