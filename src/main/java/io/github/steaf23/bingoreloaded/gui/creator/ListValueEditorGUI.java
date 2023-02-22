package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.TaskListsData;
import io.github.steaf23.bingoreloaded.gui.MenuInventory;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ListValueEditorGUI extends MenuInventory
{
    private static final InventoryItem CANCEL = new InventoryItem(39, Material.REDSTONE, "" + ChatColor.RED + ChatColor.BOLD + "Cancel");
    private static final InventoryItem SAVE = new InventoryItem(41, Material.DIAMOND, "" + ChatColor.AQUA + ChatColor.BOLD + "Save");
    private static final InventoryItem INFO = new InventoryItem(0, Material.MAP,
            ChatColor.BOLD + "Edit list values",
            "Here you can change how often",
            "an item from this list ",
            "can appear on a card.",
            ChatColor.GRAY + "Left click - increase value",
            ChatColor.GRAY + "Right click - decrease value");
    private final InventoryItem minCounter = new InventoryItem(20, Material.TARGET, " ");
    private final InventoryItem maxCounter = new InventoryItem(24, Material.TARGET, " ");

    private final CardEditorUI cardEditor;

    public int minCount = BingoCardsData.MIN_ITEMS;
    public int maxCount = BingoCardsData.MAX_ITEMS;
    private final String listName;

    public ListValueEditorGUI(CardEditorUI parent, String listName, int maxStart, int minStart)
    {
        super(45, "Updating Values", parent);
        this.cardEditor = parent;
        this.listName = listName;

        updateMax(maxStart);
        updateMin(minStart);

        fillOptions(INFO, minCounter, maxCounter, CANCEL, SAVE);
    }

    @Override
    public void delegateClick(final InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == maxCounter.getSlot())
        {
            if (clickType.isLeftClick())
            {
                updateMax(maxCount + 1);
            }
            else if (clickType.isRightClick())
            {
                updateMax(maxCount - 1);
            }
        }
        if (slotClicked == minCounter.getSlot())
        {
            if (clickType.isLeftClick())
            {
                updateMin(minCount + 1);
            }
            else if (clickType.isRightClick())
            {
                updateMin(minCount - 1);
            }
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

    public void updateMax(int newValue)
    {
        // Set the max count to be between MIN_ITEMS and the amount of tasks in that list if it's smaller than MAX_ITEMS.
        maxCount = Math.floorMod(newValue - minCount, Math.max(1, Math.min(BingoCardsData.MAX_ITEMS, TaskListsData.getTasks(listName).size())) - minCount + 1) + minCount;
        maxCounter.setAmount(maxCount);
        ItemMeta meta = maxCounter.getItemMeta();

        if (meta == null) return;
        meta.setDisplayName(TITLE_PREFIX + maxCount);
        meta.setLore(List.of("Not more than " + maxCount + " item(s) ", "will be picked from this list"));
        maxCounter.setItemMeta(meta);
        addOption(maxCounter);
    }

    public void updateMin(int newValue)
    {
        minCount = Math.floorMod(newValue - 1, maxCount) + 1;
        minCounter.setAmount(minCount);
        ItemMeta meta = minCounter.getItemMeta();

        if (meta == null) return;
        meta.setDisplayName(TITLE_PREFIX + minCount);
        meta.setLore(List.of("Not less than " + minCount + " item(s) ", "will be picked from this list"));
        minCounter.setItemMeta(meta);
        addOption(minCounter);
    }

    private void setValueForList()
    {
        BingoCardsData.setList(cardEditor.cardName, listName, maxCount, minCount);
        cardEditor.updateCardDisplay();
    }
}
