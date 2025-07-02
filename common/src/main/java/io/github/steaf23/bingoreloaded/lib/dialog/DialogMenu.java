package io.github.steaf23.bingoreloaded.lib.dialog;

import com.github.retrooper.packetevents.protocol.dialog.Dialog;
import io.github.steaf23.bingoreloaded.lib.PlayerDisplay;
import io.github.steaf23.bingoreloaded.lib.inventory.Menu;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoard;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public abstract class DialogMenu implements Menu {

	private final MenuBoard menuBoard;

	public DialogMenu(MenuBoard menuBoard) {
		this.menuBoard = menuBoard;
	}

	public void open(HumanEntity player) {
		beforeOpening(player);
	}

	@Override
	public MenuBoard getMenuBoard() {
		return menuBoard;
	}

	public abstract Dialog getDialog();

	@Override
	public void beforeOpening(HumanEntity player) {
		if (!(player instanceof Player actualPlayer)) {
			return;
		}
		PlayerDisplay.showDialog(actualPlayer, getDialog());
	}

	@Override
	public void beforeClosing(HumanEntity player) {
	}

	@Override
	public boolean onClick(InventoryClickEvent event, HumanEntity player, int clickedSlot, ClickType clickType) {
		return true;
	}

	@Override
	public boolean onDrag(InventoryDragEvent event) {
		return true;
	}

	@Override
	public boolean openOnce() {
		return false;
	}

	@Override
	public void setOpenOnce(boolean value) {
	}
}
