package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.TaskListData;
import io.github.steaf23.bingoreloaded.gui.base.*;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.List;

// This class is used to navigate through the cards and lists.
// Uses a double ListPicker, one for cards and one for lists.
public class BingoCreatorUI extends MenuInventory
{
    private final BingoCardData cardsData;
    public static final MenuItem CARD = new MenuItem(11, Material.FILLED_MAP, TITLE_PREFIX + "Edit Cards", "Click to view and edit bingo cards!");
    public static final MenuItem LIST = new MenuItem(15, Material.PAPER, TITLE_PREFIX + "Edit Lists", "Click to view and edit bingo lists!");

    public BingoCreatorUI(MenuInventory parent)
    {
        super(27, "Card Creator", parent);
        addItems(CARD, LIST);
        this.cardsData = new BingoCardData();
    }

    @Override
    public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == CARD.getSlot())
        {
            PaginatedPickerMenu cardPicker = new PaginatedPickerMenu(new ArrayList<>(), "Choose A Card", this, FilterType.DISPLAY_NAME)
            {
                private static final MenuItem CREATE_CARD = new MenuItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "New Card");

                @Override
                public void handleOpen(final InventoryOpenEvent event)
                {
                    super.handleOpen(event);
                    addItem(CREATE_CARD);
                    clearItems();

                    List<MenuItem> items = new ArrayList<>();
                    for (String card : cardsData.getCardNames())
                    {
                        MenuItem item = new MenuItem(Material.FILLED_MAP, card,
                                "This card contains " + cardsData.getListNames(card).size() + " list(s)",
                                ChatColor.GRAY + "Right-click for more options");
                        items.add(item);
                    }
                    addPickerContents(items.toArray(new MenuItem[]{}));
                }

                @Override
                public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
                {
                    if (slotClicked == CREATE_CARD.getSlot())
                    {
                        createCard(player);
                    }
                    super.onItemClicked(event, slotClicked, player, clickType);
                }

                @Override
                public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, Player player)
                {
                    if (event.getClick() == ClickType.LEFT)
                    {
                        openCardEditor(clickedOption.getItemMeta().getDisplayName(), player);
                    }
                    else if (event.getClick() == ClickType.RIGHT)
                    {
                        createCardContext(clickedOption.getItemMeta().getDisplayName(), player, this);
                    }
                }
            };
            cardPicker.open(player);
        }
        else if (slotClicked == LIST.getSlot())
        {
            PaginatedPickerMenu listPicker = new PaginatedPickerMenu(new ArrayList<>(), "Choose A List", this, FilterType.DISPLAY_NAME)
            {
                private static final MenuItem CREATE_LIST = new MenuItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "New List");

                @Override
                public void handleOpen(final InventoryOpenEvent event)
                {
                    super.handleOpen(event);
                    TaskListData listsData = cardsData.lists();

                    addItem(CREATE_LIST);
                    clearItems();

                    List<MenuItem> items = new ArrayList<>();
                    for (String list : listsData.getListNames())
                    {
                        MenuItem item = new MenuItem(Material.PAPER, list,
                                "This list contains " + listsData.getTaskCount(list) + " tasks",
                                ChatColor.GRAY + "Right-click for more options");
                        items.add(item);
                    }
                    addPickerContents(items.toArray(new MenuItem[]{}));
                }

                @Override
                public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
                {
                    if (slotClicked == CREATE_LIST.getSlot())
                    {
                        createList(player);
                    }
                    super.onItemClicked(event, slotClicked, player, clickType);
                }

                @Override
                public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, Player player)
                {
                    if (event.getClick() == ClickType.LEFT)
                    {
                        openListEditor(clickedOption.getItemMeta().getDisplayName(), player);
                    }
                    else if (event.getClick() == ClickType.RIGHT)
                    {
                        createListContext(clickedOption.getItemMeta().getDisplayName(), player, this);
                    }
                }
            };
            listPicker.open(player);
        }
    }

    private void openCardEditor(String cardName, Player player)
    {
        CardEditorUI editor = new CardEditorUI(cardName, this, cardsData);
        editor.open(player);
    }

    private void openListEditor(String listName, Player player)
    {
        ListEditorUI editor = new ListEditorUI(listName, this);
        editor.open(player);
    }

    public void createCard(Player player)
    {
        UserInputMenu.open("Enter new card name", (input) -> {
            if (!input.equals(""))
                openCardEditor(input.toLowerCase().replace(" ", "_"), player);
        }, player, this);
    }

    public void createList(Player player)
    {
        UserInputMenu.open("Enter new list name", (input) -> {
            if (!input.equals(""))
                openListEditor(input.toLowerCase().replace(" ", "_"), player);
        }, player, this);
    }

    public void createCardContext(String cardName, Player player, MenuInventory parent)
    {
        new ContextMenu(cardName, parent)
                .addAction("Remove", Material.BARRIER, (clickType) -> {
                    cardsData.removeCard(cardName);
                    return true;
                })
                .addAction("Duplicate", Material.SHULKER_SHELL, (clickType) -> {
                    cardsData.duplicateCard(cardName);
                    return true;
                })
                .addAction("Change Name", Material.NAME_TAG, (clickType) -> {
                    UserInputMenu.open("Change name to", (input) -> {
                        cardsData.renameCard(cardName, input);
                        }, player, parent, cardName);
                    return false;
                })
                .open(player);
    }

    public void createListContext(String listName, Player player, MenuInventory parent)
    {
        TaskListData listsData = cardsData.lists();

        new ContextMenu(listName, parent)
                .addAction("Remove", Material.BARRIER, (clickType) -> {
                    listsData.removeList(listName);
                    return true;
                })
                .addAction("Duplicate", Material.SHULKER_SHELL, (clickType) -> {
                    listsData.duplicateList(listName);
                    return true;
                })
                .addAction("Change Name", Material.NAME_TAG, (clickType) -> {
                    UserInputMenu.open("Change name to", (input) -> {
                        listsData.renameList(listName, input);
                    }, player, parent, listName);
                    return false;
                })
                .open(player);
    }
}