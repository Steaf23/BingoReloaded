package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.gui.base.FilterType;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedSelectionMenu;
import org.bukkit.ChatColor;
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
    private static final MenuItem ADD_LIST = new MenuItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "Add Item List", "");

    public CardEditorMenu(MenuManager menuManager, String cardName, BingoCardData cardsData)
    {
        super(menuManager, "Editing '" + cardName + "'", new ArrayList<>(), FilterType.DISPLAY_NAME);
        this.cardName = cardName;
        this.cardsData = cardsData;
        addAction(ADD_LIST, p -> createListPicker(result -> {
            cardsData.setList(cardName, result, cardsData.lists().getTaskCount(result), 1);
        }).open(p));
    }

    @Override
    public void onOptionClickedDelegate(final InventoryClickEvent event, MenuItem clickedOption, HumanEntity player)
    {
        //if an ItemList attached to a card was clicked on exists
        if (clickedOption.getItemMeta() == null) return;

        String listName = clickedOption.getItemMeta().getDisplayName();
        if (event.getClick() == ClickType.LEFT)
        {
            new ListValueEditorMenu(getMenuManager(), this, listName,
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

        List<MenuItem> newItems = new ArrayList<>();
        for (String listName : cardsData.getListNames(cardName))
        {
            MenuItem item = new MenuItem(Material.MAP, listName,
                    "This list contains " + cardsData.lists().getTaskCount(listName) + " task(s)",
                    ChatColor.GRAY + "Left-click to edit distribution",
                    ChatColor.GRAY + "Right-click to remove this list");
            item.setAmount(Math.max(1, cardsData.getListMax(cardName, listName)));
            newItems.add(item);
        }

        addItemsToSelect(newItems.toArray(new MenuItem[0]));

        applyFilter(getFilter());
    }

    private BasicMenu createListPicker(Consumer<String> result) {
        List<MenuItem> items = new ArrayList<>();
        for (String listName : cardsData.lists().getListNames())
        {
            items.add(new MenuItem(Material.PAPER, listName,
                    "This list contains " + cardsData.lists().getTaskCount(listName) + " task(s)",
                    ChatColor.GRAY + "Click to select").setCompareKey(listName));
        }

        return new PaginatedSelectionMenu(CardEditorMenu.this.getMenuManager(), "Pick A List", items, FilterType.DISPLAY_NAME)
        {
            @Override
            public void onOptionClickedDelegate(final InventoryClickEvent event, MenuItem clickedOption, HumanEntity player)
            {
                result.accept(clickedOption.getCompareKey());
                close(player);
            }
        };
    }
}
