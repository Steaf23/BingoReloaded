package io.github.steaf23.bingoreloaded.lib.inventory;

import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import net.kyori.adventure.key.Key;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public interface Menu
{
    MenuBoard getMenuBoard();

    void beforeOpening(PlayerHandle player);

    /**
     * Implementations should return if the event should be cancelled.
     * @return true if the event should be cancelled.
     */
    boolean onClick(final InventoryClickEvent event, PlayerHandle player, int clickedSlot, ClickType clickType);

    /**
     * Implementations should return if the event should be cancelled.
     * @return true if the event should be cancelled.
     */
    boolean onDrag(final InventoryDragEvent event);

    void onCustomAction(Key key, DataStorage payload);

    void beforeClosing(PlayerHandle player);

    /**
     * @return true if this menu should be removed if another menu opens on top of it.
     */
    boolean openOnce();

    void setOpenOnce(boolean value);
}
