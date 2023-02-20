package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.item.InventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class PictureUI extends AbstractGUIInventory
{
    public PictureUI(AbstractGUIInventory parent)
    {
        super(54, "§f七七七七七七七七ㇺ", parent);

        InventoryItem item = new InventoryItem(Material.MAP, "", "");
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(1010);
        item.setItemMeta(meta);
        fillOptions(item.inSlot(0, 2), item.inSlot(0, 3), item.inSlot(0, 4),
                item.inSlot(1, 2), item.inSlot(1, 3), item.inSlot(1, 4));
        addOption(item.inSlot(0, 2));
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {

    }
}
