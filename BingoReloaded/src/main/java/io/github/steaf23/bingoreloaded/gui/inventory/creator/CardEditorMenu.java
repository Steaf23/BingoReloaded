package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.easymenulib.inventory.*;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CardEditorMenu extends PaginatedSelectionMenu
{
    public final String cardName;
    public final BingoCardData cardsData;
    private static final ItemTemplate ADD_LIST = new ItemTemplate(51, Material.EMERALD, Component.text("Add Item List")
            .color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

    public CardEditorMenu(MenuBoard menuBoard, String cardName, BingoCardData cardsData)
    {
        super(menuBoard, Component.text("Editing '" + cardName + "'"), new ArrayList<>(), FilterType.DISPLAY_NAME);
        this.cardName = cardName;
        this.cardsData = cardsData;
        addAction(ADD_LIST, p -> createListPicker(result -> {
            cardsData.setList(cardName, result, cardsData.lists().getTaskCount(result), 1);
        }).open(p));
    }

    @Override
    public void onOptionClickedDelegate(final InventoryClickEvent event, ItemTemplate clickedOption, HumanEntity player)
    {
        String listName = clickedOption.getPlainTextName();
        //if an ItemList attached to a card was clicked on exists
        if (listName.isEmpty()) {
            return;
        }

        if (event.getClick() == ClickType.LEFT)
        {
            new ListValueEditorMenu(getMenuBoard(), this, listName,
                    cardsData.getListMax(cardName, listName),
                    cardsData.getListMin(cardName, listName)).open(player);
        }
        else if (event.getClick() == ClickType.RIGHT)
        {
            cardsData.removeList(cardName, listName);
            updateCardDisplay();
        }
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        super.beforeOpening(player);
        updateCardDisplay();
    }

    public void updateCardDisplay()
    {
        clearItems();

        List<ItemTemplate> newItems = new ArrayList<>();
        for (String listName : cardsData.getListNames(cardName))
        {
            ItemTemplate item = new ItemTemplate(Material.MAP, Component.text(listName),
                    Component.text("This list contains " + cardsData.lists().getTaskCount(listName) + " task(s)"));
            item.addDescription("input", 5,
                    Menu.INPUT_LEFT_CLICK + "edit distribution",
                    Menu.INPUT_RIGHT_CLICK + "remove this list");
            item.setAmount(Math.max(1, cardsData.getListMax(cardName, listName)));
            newItems.add(item);
        }

        addItemsToSelect(newItems);

        applyFilter(getFilter());
    }

    private BasicMenu createListPicker(Consumer<String> result) {
        List<ItemTemplate> items = new ArrayList<>();
        for (String listName : cardsData.lists().getListNames())
        {
            items.add(new ItemTemplate(Material.PAPER, Component.text(listName),
                    Component.text("This list contains " + cardsData.lists().getTaskCount(listName) + " task(s)"),
                    Component.text("Click to select").color(NamedTextColor.GRAY)).setCompareKey(listName));
        }

        return new PaginatedSelectionMenu(CardEditorMenu.this.getMenuBoard(), Component.text("Pick A List"), items, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(final InventoryClickEvent event, ItemTemplate clickedOption, HumanEntity player)
            {
                result.accept(clickedOption.getCompareKey());
                close(player);
            }
        };
    }
}
