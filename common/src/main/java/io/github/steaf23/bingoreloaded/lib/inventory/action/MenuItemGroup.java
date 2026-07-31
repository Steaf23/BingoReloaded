package io.github.steaf23.bingoreloaded.lib.inventory.action;

import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.ClickType;
import io.github.steaf23.bingoreloaded.lib.inventory.Menu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a container for items that can have MenuActions when clicked.
 */
public class MenuItemGroup {

    public record MenuSlot(ItemTemplate item, @Nullable MenuAction action) {}

    private final Map<Integer, MenuSlot> slots = new HashMap<>();

    public boolean handleClick(Menu menu, PlayerHandle player, int clickedSlot, ClickType clickType) {
        MenuSlot slot = slots.get(clickedSlot);

        if (slot == null) {
            return true;
        }

        if (slot.action() != null) {
            slot.action().use(new MenuAction.ActionArguments(menu, player, clickType));
        }

        // Rebuild in case the action modified the ItemTemplate.
        menu.getBackedInventory().setItem(clickedSlot, slot.item().buildItem(menu.getMenuBoard().context().server()));

        return true;
    }

    public void addItem(@NotNull ItemTemplate item, @Nullable MenuAction action) {
        if (action != null) {
            action.setItem(item);
        }

        slots.put(item.getSlot(), new MenuSlot(item, action));
    }

    public void removeItem(int slot) {
        slots.remove(slot);
    }

    public void setItemAction(int slot, @NotNull MenuAction action) {
        MenuSlot existing = slots.get(slot);

        if (existing == null) {
            throw new IllegalArgumentException("No item exists in slot " + slot);
        }

        action.setItem(existing.item());

        slots.put(slot, new MenuSlot(existing.item(), action));
    }

    public @Nullable ItemTemplate getItem(int slot) {
        MenuSlot menuSlot = slots.get(slot);
        return menuSlot != null ? menuSlot.item() : null;
    }

    public @Nullable MenuAction getAction(int slot) {
        MenuSlot menuSlot = slots.get(slot);
        return menuSlot != null ? menuSlot.action() : null;
    }

    public boolean hasItem(int slot) {
        return slots.containsKey(slot);
    }

    public Collection<MenuSlot> slots() {
        return slots.values();
    }
}
