package io.github.steaf23.bingoreloaded.gui.inventory.core;

import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;


public interface InventoryMenu extends Menu {
	@NotNull
	Inventory getInventory();

	default void openInventory(PlayerHandle player) {
		PlatformResolver.get().runTask(player.world().uniqueId(), task -> player.openInventory(new InventoryHandlePaper(getInventory())));
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
