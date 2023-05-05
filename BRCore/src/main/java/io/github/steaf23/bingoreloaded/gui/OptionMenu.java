package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OptionMenu extends MenuInventory
{
    private final Map<String, Runnable> options;

    public OptionMenu(int size, String title, MenuInventory parent)
    {
        super(size, title, parent);
        this.options = new HashMap<>();
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        InventoryItem clickedItem = new InventoryItem(event.getCurrentItem());
        if (options.containsKey(clickedItem.getKey()))
        {
            options.get(clickedItem.getKey()).run();
        }
    }

    public void addOption(String option, InventoryItem button, Runnable result)
    {
        button.setKey(option);
        addOption(button);
        options.put(button.getKey(), result);
    }
}
