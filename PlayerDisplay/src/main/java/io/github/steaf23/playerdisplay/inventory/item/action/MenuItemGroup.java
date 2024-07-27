package io.github.steaf23.playerdisplay.inventory.item.action;

import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
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

    public boolean handleClick(final InventoryClickEvent event, HumanEntity player, int clickedSlot, ClickType clickType) {
        for (ItemTemplate item : new ArrayList<>(items)) {
            if (item.getSlot() == clickedSlot) {
                item.useItem(new MenuAction.ActionArguments(player, clickType));
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
