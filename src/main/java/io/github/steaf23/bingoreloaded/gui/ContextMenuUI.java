package io.github.steaf23.bingoreloaded.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ContextMenuUI extends AbstractGUIInventory
{
    public ContextMenuUI(int size, String title, AbstractGUIInventory parent)
    {
        super(size, title, parent);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {

    }

    public static void openContext()
    {

    }
}
