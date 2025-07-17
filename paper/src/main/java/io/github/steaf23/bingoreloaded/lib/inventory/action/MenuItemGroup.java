package io.github.steaf23.bingoreloaded.lib.inventory.action;

import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.inventory.InventoryMenu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a container for items that can have MenuActions when clicked.
 */
public class MenuItemGroup
{
    public final Map<Integer, MenuAction> actions;
    public final List<ItemTemplate> items;

    public MenuItemGroup() {
		this.items = new ArrayList<>();
		this.actions = new HashMap<>();
    }

    public boolean handleClick(InventoryMenu menu, final InventoryClickEvent event, PlayerHandle player, int clickedSlot, ClickType clickType) {
        for (ItemTemplate item : new ArrayList<>(items)) {
            int itemSlot = item.getSlot();
            if (itemSlot == clickedSlot) {
                if (actions.containsKey(item.getSlot())) {
                    actions.get(itemSlot).use(new MenuAction.ActionArguments(menu, player, clickType));
                }
                //TODO: find a way to update itemstack automatically on change, no matter where!
                event.getInventory().setItem(itemSlot, ((StackHandlePaper)item.buildItem()).handle());
            }
        }
        return true;
    }

    public void addItem(ItemTemplate item, @Nullable MenuAction action) {
        items.removeIf(i -> i.getSlot() == item.getSlot());
        items.add(item);
        actions.put(item.getSlot(), action);
    }

    public void setItemAction(int slot, @NotNull MenuAction action) {
        actions.put(slot, action);
    }

    public void removeItem(int slot) {
        items.removeIf(i -> i.getSlot() == slot);
        actions.remove(slot);
    }
}
