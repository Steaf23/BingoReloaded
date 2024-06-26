package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.TaskListData;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.inventory.*;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

// This class is used to navigate through the cards and lists.
// Uses a double ListPicker, one for cards and one for lists.
public class BingoCreatorMenu extends BasicMenu
{
    private final BingoCardData cardsData;
    public static final ItemTemplate CARD = new ItemTemplate(11, Material.FILLED_MAP, TITLE_PREFIX + "Edit Cards", "Click to view and edit bingo cards!");
    public static final ItemTemplate LIST = new ItemTemplate(15, Material.PAPER, TITLE_PREFIX + "Edit Lists", "Click to view and edit bingo lists!");

    public BingoCreatorMenu(MenuBoard manager) {
        super(manager, "Card Creator", 3);
        addAction(CARD, p -> createCardPicker().open(p));
        addAction(LIST, p -> createListPicker().open(p));
        this.cardsData = new BingoCardData();
    }

    private BasicMenu createCardPicker() {
        return new PaginatedSelectionMenu(getMenuBoard(), "Choose A Card", new ArrayList<>(), FilterType.DISPLAY_NAME)
        {
            private static final ItemTemplate CREATE_CARD = new ItemTemplate(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "New Card");

            @Override
            public void beforeOpening(HumanEntity player) {
                addAction(CREATE_CARD, args -> createCard(args.player()));
                clearItems();

                List<ItemTemplate> items = new ArrayList<>();
                for (String card : cardsData.getCardNames()) {
                    ItemTemplate item = new ItemTemplate(Material.FILLED_MAP, card,
                            "This card contains " + cardsData.getListNames(card).size() + " list(s)");
                            item.addDescription("input", 5, Menu.INPUT_RIGHT_CLICK + "more options");
                    items.add(item);
                }
                addItemsToSelect(items);
            }

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, HumanEntity player) {
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
            private static final ItemTemplate CREATE_LIST = new ItemTemplate(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "New List");

            @Override
            public void beforeOpening(HumanEntity player) {
                TaskListData listsData = new TaskListData();

                addAction(CREATE_LIST, p -> createList(player));
                clearItems();

                List<ItemTemplate> items = new ArrayList<>();
                for (String list : listsData.getListNames()) {
                    ItemTemplate item = new ItemTemplate(Material.PAPER, list,
                            "This list contains " + listsData.getTaskCount(list) + " tasks");
                    item.addDescription("input", 5, Menu.INPUT_RIGHT_CLICK + "more options");
                    items.add(item);
                }
                addItemsToSelect(items);
            }

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, HumanEntity player) {
                if (event.getClick() == ClickType.LEFT) {
                    openListEditor(clickedOption.getName(), player);
                } else if (event.getClick() == ClickType.RIGHT) {
                    createListContext(clickedOption.getName()).open(player);
                }
            }
        };
    }

    private void openCardEditor(String cardName, HumanEntity player) {
        if (BingoCardData.DEFAULT_CARD_NAMES.contains(cardName)) {
            Message.sendDebug("Cannot edit default card, use right click to duplicate them instead!", (Player)player);
            Message.error("Cannot edit default card, use right click to duplicate them instead!");
            return;
        }
        CardEditorMenu editor = new CardEditorMenu(getMenuBoard(), cardName, cardsData);
        editor.open(player);
    }

    private void openListEditor(String listName, HumanEntity player) {
        if (TaskListData.DEFAULT_LIST_NAMES.contains(listName)) {
            Message.sendDebug("Cannot edit default lists, use right click to duplicate them instead!", (Player)player);
            Message.error("Cannot edit default lists, use right click to duplicate them instead!");
            return;
        }
        ListEditorMenu editor = new ListEditorMenu(getMenuBoard(), listName);
        editor.open(player);
    }

    public void createCard(HumanEntity player) {
        new UserInputMenu(getMenuBoard(), "Enter new card name", (input) -> {
            if (!input.equals(""))
                openCardEditor(input.toLowerCase().replace(" ", "_"), player);
        }, "name")
                .open(player);
    }

    public void createList(HumanEntity player) {
        new UserInputMenu(getMenuBoard(), "Enter new list name", (input) -> {
            if (!input.equals(""))
                openListEditor(input.toLowerCase().replace(" ", "_"), player);
        }, "name")
                .open(player);
    }

    public BasicMenu createCardContext(String cardName) {
        BasicMenu context = new BasicMenu(getMenuBoard(), cardName, 1);
        context.addAction(new ItemTemplate(0, Material.BARRIER, TITLE_PREFIX + "Remove"), (args) -> {
                    cardsData.removeCard(cardName);
                    context.close(args);
                })
                .addAction(new ItemTemplate(1, Material.SHULKER_SHELL, TITLE_PREFIX + "Duplicate"), (args) -> {
                    cardsData.duplicateCard(cardName);
                    context.close(args);
                })
                .addAction(new ItemTemplate(2, Material.NAME_TAG, TITLE_PREFIX + "Change Name"), (args) -> {
                    new UserInputMenu(getMenuBoard(), "Change name to", (input) -> {
                        cardsData.renameCard(cardName, input);
                        context.close(args);
                    }, cardName)
                            .open(args.player());
                })
                .addCloseAction(new ItemTemplate(8, Material.DIAMOND, TITLE_PREFIX + BingoTranslation.MENU_EXIT.translate()));
        return context;
    }

    public BasicMenu createListContext(String listName) {
        TaskListData listsData = cardsData.lists();

        BasicMenu context = new BasicMenu(getMenuBoard(), listName, 1);
        context.addAction(new ItemTemplate(0, Material.BARRIER, TITLE_PREFIX + "Remove"), (args) -> {
                    listsData.removeList(listName);
                    context.close(args);
                })
                .addAction(new ItemTemplate(1, Material.SHULKER_SHELL, TITLE_PREFIX + "Duplicate"), (args) -> {
                    listsData.duplicateList(listName);
                    context.close(args);
                })
                .addAction(new ItemTemplate(2, Material.NAME_TAG, TITLE_PREFIX + "Change Name"), (args) -> {
                    new UserInputMenu(getMenuBoard(), "Change name to", (input) -> {
                        listsData.renameList(listName, input);
                        context.close(args);
                    }, listName)
                            .open(args.player());
                })
                .addCloseAction(new ItemTemplate(8, Material.DIAMOND, TITLE_PREFIX + "Exit"));
        return context;
    }
}