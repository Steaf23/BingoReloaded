package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import net.kyori.adventure.text.Component;

//FIXME: help
public class ListValueEditorMenu extends BasicMenu
{
//    private static final ItemTemplate CANCEL = new ItemTemplate(39, Material.REDSTONE, "" + ChatColor.RED + ChatColor.BOLD + "Cancel");
//    private static final ItemTemplate SAVE = new ItemTemplate(41, Material.DIAMOND, "" + ChatColor.AQUA + ChatColor.BOLD + "Save");
//    private static final ItemTemplate INFO = new ItemTemplate(0, Material.MAP,
//            ChatColor.BOLD + "Edit list values",
//            "Here you can change how often",
//            "an item from this list ",
//            "can appear on a card.",
//            ChatColor.GRAY + "Left click - increase value",
//            ChatColor.GRAY + "Right click - decrease value");
//
//    private final CardEditorMenu cardEditor;
//
//    public int minCount = BingoCardData.MIN_ITEMS;
//    public int maxCount = BingoCardData.MAX_ITEMS;
//    private final String listName;
//
    public ListValueEditorMenu(MenuBoard menuBoard, CardEditorMenu parent, String listName, int maxStart, int minStart) {
        super(menuBoard, Component.text("Updating Values"), 6);
//        this.cardEditor = parent;
//        this.listName = listName;
//
//        this.minCount = minStart;
//        this.maxCount = maxStart;
//        ItemTemplate minCounter = new ItemTemplate(20, Material.TARGET, " ");
//        minCounter.setName(TextComponent.fromLegacy(TITLE_PREFIX + minCount));
//        minCounter.setLore(ChatComponentUtils.createComponentsFromString("Not less than " + minCount + " item(s) ", "will be picked from this list"));
//        addAction(minCounter, new SpinBoxButtonAction(BingoCardData.MIN_ITEMS, maxCount, minStart, value -> {
//            minCounter.setName(TextComponent.fromLegacy(TITLE_PREFIX + value));
//            minCounter.setLore(ChatComponentUtils.createComponentsFromString("Not less than " + value + " item(s) ", "will be picked from this list"));
//            minCount = value;
    }
//
//        ItemTemplate maxCounter = new ItemTemplate(24, Material.TARGET, " ");
//        maxCounter.setName(TextComponent.fromLegacy(TITLE_PREFIX + maxCount));
//        maxCounter.setLore(ChatComponentUtils.createComponentsFromString("Not more than " + maxCount + " item(s) ", "will be picked from this list"));
//        addAction(maxCounter, new SpinBoxButtonAction(minCount, Math.min(BingoCardData.MAX_ITEMS, cardEditor.cardsData.lists().getTaskCount(listName)), maxStart, value -> {
//            maxCounter.setName(TextComponent.fromLegacy(TITLE_PREFIX + value));
//            maxCounter.setLore(ChatComponentUtils.createComponentsFromString("Not more than " + value + " item(s) ", "will be picked from this list"));
//            maxCount = value;
//        }));
//
//        addAction(SAVE, p -> {
//            setValueForList();
//            close(p);
//        });
//        addItem(INFO);
//        addCloseAction(CANCEL);
//    }
//
//    private void setValueForList() {
//        cardEditor.cardsData.setList(cardEditor.cardName, listName, maxCount, minCount);
//        cardEditor.updateCardDisplay();
//    }
}
