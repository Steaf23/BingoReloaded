package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.TaskListData;
import io.github.steaf23.bingoreloaded.gui.base.*;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedSelectionMenu;
import io.github.steaf23.bingoreloaded.gui.base.UserInputMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

// This class is used to navigate through the cards and lists.
// Uses a double ListPicker, one for cards and one for lists.
public class BingoCreatorMenu extends BasicMenu
{
    private final BingoCardData cardsData;
    public static final MenuItem CARD = new MenuItem(11, Material.FILLED_MAP, TITLE_PREFIX + "Edit Cards", "Click to view and edit bingo cards!");
    public static final MenuItem LIST = new MenuItem(15, Material.PAPER, TITLE_PREFIX + "Edit Lists", "Click to view and edit bingo lists!");

    public BingoCreatorMenu(MenuManager manager) {
        super(manager, "Card Creator", 3);
        addAction(CARD, p -> createCardPicker().open(p));
        addAction(LIST, p -> createListPicker().open(p));
        this.cardsData = new BingoCardData();
    }

    private BasicMenu createCardPicker() {
        return new PaginatedSelectionMenu(getMenuManager(), "Choose A Card", new ArrayList<>(), FilterType.DISPLAY_NAME)
        {
            private static final MenuItem CREATE_CARD = new MenuItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "New Card");

            @Override
            public void beforeOpening(HumanEntity player) {
                addAction(CREATE_CARD, p -> createCard(p));
                clearItems();

                List<MenuItem> items = new ArrayList<>();
                for (String card : cardsData.getCardNames()) {
                    MenuItem item = new MenuItem(Material.FILLED_MAP, card,
                            "This card contains " + cardsData.getListNames(card).size() + " list(s)",
                            ChatColor.GRAY + "Right-click for more options");
                    items.add(item);
                }
                addItemsToSelect(items.toArray(new MenuItem[]{}));
            }

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, HumanEntity player) {
                if (event.getClick() == ClickType.LEFT) {
                    openCardEditor(clickedOption.getItemMeta().getDisplayName(), player);
                } else if (event.getClick() == ClickType.RIGHT) {
                    createCardContext(clickedOption.getItemMeta().getDisplayName()).open(player);
                }
            }
        };
    }

    private BasicMenu createListPicker() {
        return new PaginatedSelectionMenu(getMenuManager(), "Choose A List", new ArrayList<>(), FilterType.DISPLAY_NAME)
        {
            private static final MenuItem CREATE_LIST = new MenuItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "New List");

            @Override
            public void beforeOpening(HumanEntity player) {
                TaskListData listsData = cardsData.lists();

                addAction(CREATE_LIST, p -> createList(player));
                clearItems();

                List<MenuItem> items = new ArrayList<>();
                for (String list : listsData.getListNames()) {
                    MenuItem item = new MenuItem(Material.PAPER, list,
                            "This list contains " + listsData.getTaskCount(list) + " tasks",
                            ChatColor.GRAY + "Right-click for more options");
                    items.add(item);
                }
                addItemsToSelect(items.toArray(new MenuItem[]{}));
            }

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, HumanEntity player) {
                if (event.getClick() == ClickType.LEFT) {
                    openListEditor(clickedOption.getItemMeta().getDisplayName(), player);
                } else if (event.getClick() == ClickType.RIGHT) {
                    createListContext(clickedOption.getItemMeta().getDisplayName()).open(player);
                }
            }
        };
    }

    private void openCardEditor(String cardName, HumanEntity player) {
        CardEditorMenu editor = new CardEditorMenu(getMenuManager(), cardName, cardsData);
        editor.open(player);
    }

    private void openListEditor(String listName, HumanEntity player) {
        ListEditorMenu editor = new ListEditorMenu(getMenuManager(), listName);
        editor.open(player);
    }

    public void createCard(HumanEntity player) {
        new UserInputMenu(getMenuManager(), "Enter new card name", (input) -> {
            if (!input.equals(""))
                openCardEditor(input.toLowerCase().replace(" ", "_"), player);
        }, player, "name");
    }

    public void createList(HumanEntity player) {
        new UserInputMenu(getMenuManager(), "Enter new list name", (input) -> {
            if (!input.equals(""))
                openListEditor(input.toLowerCase().replace(" ", "_"), player);
        }, player, "name");
    }

    public BasicMenu createCardContext(String cardName) {
        BasicMenu context = new BasicMenu(getMenuManager(), cardName, 1);
        context.addAction(new MenuItem(Material.BARRIER, TITLE_PREFIX + "Remove"), (p) -> {
                    cardsData.removeCard(cardName);
                    context.close(p);
                })
                .addAction(new MenuItem(Material.SHULKER_SHELL, TITLE_PREFIX + "Duplicate"), (p) -> {
                    cardsData.duplicateCard(cardName);
                    context.close(p);
                })
                .addAction(new MenuItem(Material.NAME_TAG, TITLE_PREFIX + "Change Name"), (p) -> {
                    new UserInputMenu(getMenuManager(), "Change name to", (input) -> {
                        cardsData.renameCard(cardName, input);
                        context.close(p);
                    }, p, cardName);
                })
                .addCloseAction(new MenuItem(8, Material.DIAMOND, TITLE_PREFIX + "Exit"));
        return context;
    }

    public BasicMenu createListContext(String listName) {
        TaskListData listsData = cardsData.lists();

        BasicMenu context = new BasicMenu(getMenuManager(), listName, 1);
        context.addAction(new MenuItem(Material.BARRIER, TITLE_PREFIX + "Remove"), (p) -> {
                    listsData.removeList(listName);
                    context.close(p);
                })
                .addAction(new MenuItem(Material.SHULKER_SHELL, TITLE_PREFIX + "Duplicate"), (p) -> {
                    listsData.duplicateList(listName);
                    context.close(p);
                })
                .addAction(new MenuItem(Material.NAME_TAG, TITLE_PREFIX + "Change Name"), (p) -> {
                    new UserInputMenu(getMenuManager(), "Change name to", (input) -> {
                        listsData.renameList(listName, input);
                        context.close(p);
                    }, p, listName);
                })
                .addCloseAction(new MenuItem(8, Material.DIAMOND, TITLE_PREFIX + "Exit"));
        return context;
    }
}