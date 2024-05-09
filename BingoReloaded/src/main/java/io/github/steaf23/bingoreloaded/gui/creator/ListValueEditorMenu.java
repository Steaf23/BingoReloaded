package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.easymenulib.menu.BasicMenu;
import io.github.steaf23.easymenulib.menu.MenuBoard;
import io.github.steaf23.easymenulib.menu.item.MenuItem;
import io.github.steaf23.easymenulib.menu.item.action.SpinBoxButtonAction;
import org.bukkit.ChatColor;
import org.bukkit.Material;

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

    private final CardEditorMenu cardEditor;

    public int minCount = BingoCardData.MIN_ITEMS;
    public int maxCount = BingoCardData.MAX_ITEMS;
    private final String listName;

    public ListValueEditorMenu(MenuBoard menuBoard, CardEditorMenu parent, String listName, int maxStart, int minStart) {
        super(menuBoard, "Updating Values", 6);
        this.cardEditor = parent;
        this.listName = listName;

        MenuItem minCounter = new MenuItem(20, Material.TARGET, " ");
        addAction(minCounter, new SpinBoxButtonAction(BingoCardData.MIN_ITEMS, maxCount, minStart, value -> {
            minCounter.setName(TITLE_PREFIX + minCount);
            minCounter.setLore("Not more than" + minCount + " item(s) ", "will be picked from this list");
        }));

        MenuItem maxCounter = new MenuItem(24, Material.TARGET, " ");
        addAction(maxCounter, new SpinBoxButtonAction(minCount, Math.min(BingoCardData.MAX_ITEMS, cardEditor.cardsData.lists().getTaskCount(listName)), maxStart, value -> {
            maxCounter.setName(TITLE_PREFIX + maxCount);
            maxCounter.setLore("Not more than " + maxCount + " item(s) ", "will be picked from this list");
        }));

        addAction(SAVE, p -> {
            setValueForList();
            close(p);
        });
        addItem(INFO);
        addCloseAction(CANCEL);
    }

    private void setValueForList() {
        cardEditor.cardsData.setList(cardEditor.cardName, listName, maxCount, minCount);
        cardEditor.updateCardDisplay();
    }
}
