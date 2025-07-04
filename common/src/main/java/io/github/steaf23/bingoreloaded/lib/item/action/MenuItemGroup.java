package io.github.steaf23.bingoreloaded.lib.item.action;

import io.github.steaf23.bingoreloaded.lib.api.PlayerClickType;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.Menu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a container for items that can have MenuActions when clicked.
 */
public class MenuItemGroup
{
    public final List<ItemTemplate> items;

    public MenuItemGroup() {
        this.items = new ArrayList<>();
    }

    public boolean handleClick(Menu menu, final InventoryClickEvent event, PlayerHandle player, int clickedSlot, PlayerClickType clickType) {
        for (ItemTemplate item : new ArrayList<>(items)) {
            if (item.getSlot() == clickedSlot) {
                item.useItem(new MenuAction.ActionArguments(menu, player, clickType));
                //TODO: find a way to update itemstack automatically on change, no matter where!
                event.getInventory().setItem(item.getSlot(), item.buildItem());
            }
        }
        return true;
    }

    public void addItem(ItemTemplate item) {
        items.removeIf(i -> i.getSlot() == item.getSlot());
        items.add(item);
    }
}
