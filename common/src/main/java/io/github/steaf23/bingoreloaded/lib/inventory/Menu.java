package io.github.steaf23.bingoreloaded.lib.inventory;

import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;


public interface Menu {

	MenuType type();
	Component title();

	@NotNull
	InventoryTemplate getBackedInventory();

	Component INPUT_LEFT_CLICK = inputButtonText(Component.keybind("key.attack"));
	Component INPUT_RIGHT_CLICK = inputButtonText(Component.keybind("key.use"));
	// tutorial.punch_tree.description resolves to "Hold down %1" in English.
	Component INPUT_SHIFT_CLICK = inputButtonText(Component.translatable("tutorial.punch_tree.description", Component.translatable("key.keyboard.left.shift")));

	static Component inputButtonText(Component buttonText) {
		return Component.text()
				.append(Component.text("<").color(NamedTextColor.DARK_GRAY))
				.append(buttonText.color(NamedTextColor.GRAY))
				.append(Component.text(">").color(NamedTextColor.DARK_GRAY))
				.append(Component.text(": ").color(NamedTextColor.WHITE))
				.build();
	}

	MenuBoard getMenuBoard();

	void beforeOpening(PlayerHandle player);

	/**
	 * Implementations should return if the event should be cancelled.
	 * @return true if the event should be cancelled.
	 */
	boolean onClick(PlayerHandle player, int clickedSlot, ClickType clickType);

	/**
	 * Implementations should return if the event should be cancelled.
	 * @return true if the event should be cancelled.
	 */
	boolean onDrag();

	void beforeClosing(PlayerHandle player);

	/**
	 * @return true if this menu should be removed if another menu opens on top of it.
	 */
	boolean openOnce();

	void setOpenOnce(boolean value);
}
