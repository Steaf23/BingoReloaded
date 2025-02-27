package io.github.steaf23.playerdisplay.inventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public interface Menu
{
    MenuBoard getMenuBoard();

    void beforeOpening(HumanEntity player);

    /**
     * Implementations should return if the event should be cancelled.
     * @return true if the event should be cancelled.
     */
    boolean onClick(final InventoryClickEvent event, HumanEntity player, int clickedSlot, ClickType clickType);

    /**
     * Implementations should return if the event should be cancelled.
     * @return true if the event should be cancelled.
     */
    boolean onDrag(final InventoryDragEvent event);

    void beforeClosing(HumanEntity player);

    /**
     * @return true if this menu should be removed if another menu opens on top of it.
     */
    boolean openOnce();

    void setOpenOnce(boolean value);

    static Component inputButtonText(Component buttonText) {
        return Component.text()
                .append(Component.text("<").color(NamedTextColor.DARK_GRAY))
                .append(buttonText.color(NamedTextColor.GRAY))
                .append(Component.text(">").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(": ").color(NamedTextColor.WHITE))
                .build();
    }

    @NotNull
    Inventory getInventory();

    Component INPUT_LEFT_CLICK = inputButtonText(Component.keybind("key.attack"));
    Component INPUT_RIGHT_CLICK = inputButtonText(Component.keybind("key.use"));
    Component INPUT_SHIFT_CLICK = inputButtonText(Component.keybind("Hold Shift"));
}
