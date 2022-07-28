package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.item.InventoryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ListValueEditorGUI extends AbstractGUIInventory
{
    private static final InventoryItem HIGHER = new InventoryItem(13, Material.GUNPOWDER, "More");
    private static final InventoryItem LOWER = new InventoryItem(31, Material.HOPPER, "Less");
    private final InventoryItem counter = new InventoryItem(22, Material.TARGET, " ");
    private final InventoryItem CANCEL = new InventoryItem(20, Material.REDSTONE, "Cancel");
    private final InventoryItem SAVE = new InventoryItem(24, Material.DIAMOND, "Save");

    private final CardEditorUI cardEditor;

    public int itemCount = 0;
    private final String listName;

    public ListValueEditorGUI(CardEditorUI parent, String listName, int startingValue)
    {
        super(45, "Updating Values", parent);
        this.cardEditor = parent;
        this.listName = listName;

        updateCounter(startingValue - 1);

        fillOptions(new InventoryItem[]{HIGHER, counter, LOWER, CANCEL, SAVE});
    }

    @Override
    public void delegateClick(final InventoryClickEvent event, int slotClicked, Player player)
    {
        if (slotClicked == HIGHER.getSlot())
        {
            updateCounter(itemCount + 1);
        }
        else if (slotClicked == LOWER.getSlot())
        {
            updateCounter(itemCount - 1);
        }
        else if (slotClicked == SAVE.getSlot())
        {
            setValueForList();
            close(player);
        }
        else if (slotClicked == CANCEL.getSlot())
        {
            close(player);
        }
    }

    public void updateCounter(int newValue)
    {
        itemCount = Math.floorMod(newValue, 36);

        counter.setAmount(itemCount + 1);
        ItemMeta meta = counter.getItemMeta();

        if (meta == null) return;
        meta.setDisplayName("" + ChatColor.GOLD + ChatColor.BOLD + (itemCount + 1));
        meta.setLore(List.of("Items from this list", "will be picked at most " + (itemCount + 1) +  " time(s)"));
        counter.setItemMeta(meta);
        addOption(counter);
    }

    private void setValueForList()
    {
        cardEditor.updateListValues(listName, itemCount + 1);
    }
}
