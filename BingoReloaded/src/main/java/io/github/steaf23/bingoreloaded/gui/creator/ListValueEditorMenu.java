package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ListValueEditorMenu extends BasicMenu
{
    private static final MenuItem CANCEL = new MenuItem(39, Material.REDSTONE, "" + ChatColor.RED + ChatColor.BOLD + "Cancel");
    private static final MenuItem SAVE = new MenuItem(41, Material.DIAMOND, "" + ChatColor.AQUA + ChatColor.BOLD + "Save");
    private static final MenuItem INFO = new MenuItem(0, Material.MAP,
            ChatColor.BOLD + "Edit list values",
            "Here you can change how often",
            "an item from this list ",
            "can appear on a card.",
            ChatColor.GRAY + "Left click - increase value",
            ChatColor.GRAY + "Right click - decrease value");
    private final MenuItem minCounter = new MenuItem(20, Material.TARGET, " ");
    private final MenuItem maxCounter = new MenuItem(24, Material.TARGET, " ");

    private final CardEditorMenu cardEditor;

    public int minCount = BingoCardData.MIN_ITEMS;
    public int maxCount = BingoCardData.MAX_ITEMS;
    private final String listName;

    public ListValueEditorMenu(MenuManager menuManager, CardEditorMenu parent, String listName, int maxStart, int minStart) {
        super(menuManager, "Updating Values", 6);
        this.cardEditor = parent;
        this.listName = listName;

        updateMax(maxStart);
        updateMin(minStart);

        addItems(INFO, minCounter, maxCounter);
        addAction(SAVE, p -> {
            setValueForList();
            close(p);
        });
        addCloseAction(CANCEL);
    }

    @Override
    public boolean onClick(InventoryClickEvent event, HumanEntity player, MenuItem clickedItem, ClickType clickType) {
        if (event.getRawSlot() == maxCounter.getSlot()) {
            if (clickType.isLeftClick()) {
                updateMax(maxCount + 1);
            } else if (clickType.isRightClick()) {
                updateMax(maxCount - 1);
            }
        }
        if (event.getRawSlot() == minCounter.getSlot()) {
            if (clickType.isLeftClick()) {
                updateMin(minCount + 1);
            } else if (clickType.isRightClick()) {
                updateMin(minCount - 1);
            }
        }
        return super.onClick(event, player, clickedItem, clickType);
    }

    public void updateMax(int newValue) {
        // Set the max count to be between MIN_ITEMS and the amount of tasks in that list if it's smaller than MAX_ITEMS.
        maxCount = Math.floorMod(newValue - minCount, Math.max(1, Math.min(BingoCardData.MAX_ITEMS, cardEditor.cardsData.lists().getTaskCount(listName))) - minCount + 1) + minCount;
        maxCounter.setAmount(maxCount);
        ItemMeta meta = maxCounter.getItemMeta();

        if (meta == null) return;
        meta.setDisplayName(TITLE_PREFIX + maxCount);
        meta.setLore(List.of("Not more than " + maxCount + " item(s) ", "will be picked from this list"));
        maxCounter.setItemMeta(meta);
        addItem(maxCounter);
    }

    public void updateMin(int newValue) {
        minCount = Math.floorMod(newValue - 1, maxCount) + 1;
        minCounter.setAmount(minCount);
        ItemMeta meta = minCounter.getItemMeta();

        if (meta == null) return;
        meta.setDisplayName(TITLE_PREFIX + minCount);
        meta.setLore(List.of("Not less than " + minCount + " item(s) ", "will be picked from this list"));
        minCounter.setItemMeta(meta);
        addItem(minCounter);
    }

    private void setValueForList() {
        cardEditor.cardsData.setList(cardEditor.cardName, listName, maxCount, minCount);
        cardEditor.updateCardDisplay();
    }
}
