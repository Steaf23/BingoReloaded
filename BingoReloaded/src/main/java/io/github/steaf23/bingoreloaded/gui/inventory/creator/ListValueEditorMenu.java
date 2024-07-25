package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import io.github.steaf23.playerdisplay.inventory.item.action.SpinBoxButtonAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

public class ListValueEditorMenu extends BasicMenu
{
    private static final ItemTemplate CANCEL = new ItemTemplate(39, Material.REDSTONE, BingoMessage.MENU_EXIT.asPhrase().color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
    private static final ItemTemplate SAVE = new ItemTemplate(41, Material.DIAMOND, BingoMessage.MENU_SAVE_EXIT.asPhrase().color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
    private static final ItemTemplate INFO = new ItemTemplate(0, Material.MAP,
            Component.text("Edit list values").decorate(TextDecoration.BOLD),
            Component.text("Here you can change how often"),
            Component.text("an item from this list "),
            Component.text("can appear on a card."))
            .addDescription("input", 5,
                    INPUT_LEFT_CLICK.append(Component.text("increase value")),
                    INPUT_RIGHT_CLICK.append(Component.text("decrease value")));
    private final CardEditorMenu cardEditor;

    public int minCount;
    public int maxCount;
    private final String listName;

    public ListValueEditorMenu(MenuBoard menuBoard, CardEditorMenu parent, String listName, int maxStart, int minStart) {
        super(menuBoard, Component.text("Updating Values"), 6);
        this.cardEditor = parent;
        this.listName = listName;

        this.minCount = minStart;
        this.maxCount = maxStart;
        ItemTemplate minCounter = new ItemTemplate(20, Material.TARGET, BasicMenu.applyTitleFormat(String.valueOf(minCount)))
                .setLore(Component.text("Not less than " + minCount + " item(s)"),
                        Component.text("will be picked from this list"));
        addAction(minCounter, new SpinBoxButtonAction(BingoCardData.MIN_ITEMS, maxCount, minStart, value -> {
            minCounter.setName(BasicMenu.applyTitleFormat(String.valueOf(value)));
            minCounter.setLore(Component.text("Not less than " + value + " item(s)"),
                    Component.text("will be picked from this list"));
            minCount = value;
        }));

        ItemTemplate maxCounter = new ItemTemplate(20, Material.TARGET, BasicMenu.applyTitleFormat(String.valueOf(maxCount)))
                .setLore(Component.text("Not more than " + maxCount + " item(s)"),
                        Component.text("will be picked from this list"));
        addAction(maxCounter, new SpinBoxButtonAction(maxCount, Math.min(BingoCardData.MAX_ITEMS, cardEditor.cardsData.lists().getTaskCount(listName)), maxStart, value -> {
            minCounter.setName(BasicMenu.applyTitleFormat(String.valueOf(value)));
            minCounter.setLore(Component.text("Not more than " + value + " item(s)"),
                    Component.text("will be picked from this list"));
            maxCount = value;
        }));

        addAction(SAVE, arguments -> {
            setValueForList();
            close(arguments.player());
        });
        addItem(INFO);
        addCloseAction(CANCEL);
    }

    private void setValueForList() {
        cardEditor.cardsData.setList(cardEditor.cardName, listName, maxCount, minCount);
        cardEditor.updateCardDisplay();
    }
}
