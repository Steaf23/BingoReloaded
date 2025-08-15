package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.SpinBoxButtonAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;


public class ListValueEditorMenu extends BasicMenu
{
    private static final ItemTemplate CANCEL = new ItemTemplate(39, ItemType.of("minecraft:redstone"), BingoMessage.MENU_EXIT.asPhrase().color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
    private static final ItemTemplate SAVE = new ItemTemplate(41, ItemType.of("minecraft:diamond"), BingoMessage.MENU_SAVE_EXIT.asPhrase().color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
    private static final ItemTemplate INFO = new ItemTemplate(0, ItemType.of("minecraft:map"),
            BingoReloaded.applyTitleFormat("Edit list values"),
            Component.text("Here you can change how often"),
            Component.text("an item from this list "),
            Component.text("can appear on a card."));
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
        ItemTemplate minCounter = new ItemTemplate(2, 2, ItemType.of("minecraft:target"), BingoReloaded.applyTitleFormat(String.valueOf(minCount)))
                .setLore(Component.text("Not less than " + minCount + " item(s)"),
                        Component.text("will be picked from this list"));
        ItemTemplate maxCounter = new ItemTemplate(6, 2, ItemType.of("minecraft:target"), BingoReloaded.applyTitleFormat(String.valueOf(maxCount)))
                .setLore(Component.text("Not more than " + maxCount + " item(s)"),
                        Component.text("will be picked from this list"));

        SpinBoxButtonAction minCounterButtonAction;
        SpinBoxButtonAction maxCounterButtonAction;

        minCounterButtonAction = new SpinBoxButtonAction(BingoCardData.MIN_ITEMS, maxCount, minStart);
		minCounterButtonAction.setItem(minCounter);
        addAction(minCounterButtonAction);

        maxCounterButtonAction = new SpinBoxButtonAction(minCount, Math.min(BingoCardData.MAX_ITEMS, cardEditor.cardsData.lists().getTaskCount(listName)), maxStart, value -> {
            maxCounter.setName(BingoReloaded.applyTitleFormat(String.valueOf(value)));
            maxCounter.setLore(Component.text("Not more than " + value + " item(s)"),
                    Component.text("will be picked from this list"));
            maxCount = value;
            minCounterButtonAction.setMax(maxCount);
        });

        minCounterButtonAction.setCallback(value -> {
            minCounter.setName(BingoReloaded.applyTitleFormat(String.valueOf(value)));
            minCounter.setLore(Component.text("Not less than " + value + " item(s)"),
                    Component.text("will be picked from this list"));
            minCount = value;
            maxCounterButtonAction.setMin(minCount);
        });

		maxCounterButtonAction.setItem(maxCounter);
        addAction(maxCounterButtonAction);

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
