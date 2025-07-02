package io.github.steaf23.bingoreloaded.lib.inventory;

import io.github.steaf23.bingoreloaded.lib.PlayerDisplay;
import io.github.steaf23.bingoreloaded.lib.api.InventoryHandle;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;


public interface InventoryMenu extends Menu {
	@NotNull
	InventoryHandle getInventory();

	default void openInventory(PlayerHandle player) {
		Bukkit.getScheduler().runTask(PlayerDisplay.getExtension(), task -> player.openInventory(getInventory()));
	}

	static Component inputButtonText(Component buttonText) {
		return Component.text()
				.append(Component.text("<").color(NamedTextColor.DARK_GRAY))
				.append(buttonText.color(NamedTextColor.GRAY))
				.append(Component.text(">").color(NamedTextColor.DARK_GRAY))
				.append(Component.text(": ").color(NamedTextColor.WHITE))
				.build();
	}

	Component INPUT_LEFT_CLICK = inputButtonText(Component.keybind("key.attack"));
	Component INPUT_RIGHT_CLICK = inputButtonText(Component.keybind("key.use"));
	Component INPUT_SHIFT_CLICK = inputButtonText(Component.keybind("Hold Shift"));
}
