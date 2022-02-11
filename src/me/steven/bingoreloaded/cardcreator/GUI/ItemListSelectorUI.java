package me.steven.bingoreloaded.cardcreator.GUI;

import me.steven.bingoreloaded.GUIInventories.AbstractGUIInventory;
import me.steven.bingoreloaded.GUIInventories.ItemPickerUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListSelectorUI extends ItemPickerUI
{
    public ItemListSelectorUI(AbstractGUIInventory parent)
    {
        super(parent, null);

    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, ItemStack itemClicked, Player player)
    {

    }
}
