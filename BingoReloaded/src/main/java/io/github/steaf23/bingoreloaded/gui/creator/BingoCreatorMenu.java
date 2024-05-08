package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.TaskListData;
import io.github.steaf23.easymenulib.menu.*;
import io.github.steaf23.easymenulib.menu.item.MenuItem;
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

    public BingoCreatorMenu(MenuBoard manager) {
        super(manager, "Card Creator", 3);
        addAction(CARD, p -> createCardPicker().open(p));
        addAction(LIST, p -> createListPicker().open(p));
        this.cardsData = new BingoCardData();
    }

    private BasicMenu createCardPicker() {
        return new PaginatedSelectionMenu(getMenuBoard(), "Choose A Card", new ArrayList<>(), FilterType.DISPLAY_NAME)
        {
            private static final MenuItem CREATE_CARD = new MenuItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "New Card");

            @Override
            public void beforeOpening(HumanEntity player) {
                addAction(CREATE_CARD, args -> createCard(args.player()));
                clearItems();

                List<MenuItem> items = new ArrayList<>();
                for (String card : cardsData.getCardNames()) {
                    MenuItem item = new MenuItem(Material.FILLED_MAP, card,
                            "This card contains " + cardsData.getListNames(card).size() + " list(s)",
                            ChatColor.GRAY + "Right-click for more options");
                    items.add(item);
                }
                addItemsToSelect(items);
            }

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, HumanEntity player) {
                if (event.getClick() == ClickType.LEFT) {
                    openCardEditor(clickedOption.getName(), player);
                } else if (event.getClick() == ClickType.RIGHT) {
                    createCardContext(clickedOption.getName()).open(player);
                }
            }
        };
    }

    private BasicMenu createListPicker() {
        return new PaginatedSelectionMenu(getMenuBoard(), "Choose A List", new ArrayList<>(), FilterType.DISPLAY_NAME)
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
                addItemsToSelect(items);
            }

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, HumanEntity player) {
                if (event.getClick() == ClickType.LEFT) {
                    openListEditor(clickedOption.getName(), player);
                } else if (event.getClick() == ClickType.RIGHT) {
                    createListContext(clickedOption.getName()).open(player);
                }
            }
        };
    }

    private void openCardEditor(String cardName, HumanEntity player) {
        CardEditorMenu editor = new CardEditorMenu(getMenuBoard(), cardName, cardsData);
        editor.open(player);
    }

    private void openListEditor(String listName, HumanEntity player) {
        ListEditorMenu editor = new ListEditorMenu(getMenuBoard(), listName);
        editor.open(player);
    }

    public void createCard(HumanEntity player) {
        new UserInputMenu(getMenuBoard(), "Enter new card name", (input) -> {
            if (!input.equals(""))
                openCardEditor(input.toLowerCase().replace(" ", "_"), player);
        }, player, "name");
    }

    public void createList(HumanEntity player) {
        new UserInputMenu(getMenuBoard(), "Enter new list name", (input) -> {
            if (!input.equals(""))
                openListEditor(input.toLowerCase().replace(" ", "_"), player);
        }, player, "name");
    }

    public BasicMenu createCardContext(String cardName) {
        BasicMenu context = new BasicMenu(getMenuBoard(), cardName, 1);
        context.addAction(new MenuItem(Material.BARRIER, TITLE_PREFIX + "Remove"), (args) -> {
                    cardsData.removeCard(cardName);
                    context.close(args);
                })
                .addAction(new MenuItem(Material.SHULKER_SHELL, TITLE_PREFIX + "Duplicate"), (args) -> {
                    cardsData.duplicateCard(cardName);
                    context.close(args);
                })
                .addAction(new MenuItem(Material.NAME_TAG, TITLE_PREFIX + "Change Name"), (args) -> {
                    new UserInputMenu(getMenuBoard(), "Change name to", (input) -> {
                        cardsData.renameCard(cardName, input);
                        context.close(args);
                    }, args.player(), cardName);
                })
                .addCloseAction(new MenuItem(8, Material.DIAMOND, TITLE_PREFIX + "Exit"));
        return context;
    }

    public BasicMenu createListContext(String listName) {
        TaskListData listsData = cardsData.lists();

        BasicMenu context = new BasicMenu(getMenuBoard(), listName, 1);
        context.addAction(new MenuItem(Material.BARRIER, TITLE_PREFIX + "Remove"), (args) -> {
                    listsData.removeList(listName);
                    context.close(args);
                })
                .addAction(new MenuItem(Material.SHULKER_SHELL, TITLE_PREFIX + "Duplicate"), (args) -> {
                    listsData.duplicateList(listName);
                    context.close(args);
                })
                .addAction(new MenuItem(Material.NAME_TAG, TITLE_PREFIX + "Change Name"), (args) -> {
                    new UserInputMenu(getMenuBoard(), "Change name to", (input) -> {
                        listsData.renameList(listName, input);
                        context.close(args);
                    }, args.player(), listName);
                })
                .addCloseAction(new MenuItem(8, Material.DIAMOND, TITLE_PREFIX + "Exit"));
        return context;
    }
}