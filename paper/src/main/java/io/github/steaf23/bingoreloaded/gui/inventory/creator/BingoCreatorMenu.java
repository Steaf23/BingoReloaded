package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.TaskListData;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.FilterType;
import io.github.steaf23.bingoreloaded.lib.inventory.InventoryMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedSelectionMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.UserInputMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

// This class is used to navigate through the cards and lists.
// Uses a double ListPicker, one for cards and one for lists.
public class BingoCreatorMenu extends BasicMenu
{
    private final BingoCardData cardsData;
    public static final ItemTemplate CARD = new ItemTemplate(11, ItemTypePaper.of(Material.FILLED_MAP), BingoReloaded.applyTitleFormat("Edit Cards"), Component.text("Click to view and edit bingo cards!"));
    public static final ItemTemplate LIST = new ItemTemplate(15, ItemTypePaper.of(Material.PAPER), BingoReloaded.applyTitleFormat("Edit Lists"), Component.text("Click to view and edit bingo lists!"));

    private static final ItemType REMOVE_ICON = ItemTypePaper.of(Material.BARRIER);
    private static final ItemType COPY_ICON = ItemTypePaper.of(Material.SHULKER_SHELL);
    private static final ItemType RENAME_ICON = ItemTypePaper.of(Material.NAME_TAG);
    private static final ItemType SAVE_ICON = ItemTypePaper.of(Material.DIAMOND);

    public BingoCreatorMenu(MenuBoard manager) {
        super(manager, Component.text("Card Creator"), 3);
        this.cardsData = new BingoCardData();
        addAction(CARD, arguments -> createCardPicker().open(arguments.player()));
        addAction(LIST, arguments -> createListPicker().open(arguments.player()));
    }

    private BasicMenu createCardPicker() {
        return new PaginatedSelectionMenu(getMenuBoard(), Component.text("Choose A Card"), new ArrayList<>(), FilterType.DISPLAY_NAME)
        {
            private static final ItemTemplate CREATE_CARD = new ItemTemplate(51, ItemTypePaper.of(Material.EMERALD),
                    Component.text("New Card").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

            @Override
            public void beforeOpening(PlayerHandle player) {
                addAction(CREATE_CARD, args -> createCard(args.player()));
                clearItems();

                List<ItemTemplate> items = new ArrayList<>();
                for (String card : cardsData.getCardNames()) {
                    ItemTemplate item = new ItemTemplate(ItemTypePaper.of(Material.FILLED_MAP), Component.text(card),
                            Component.text("This card contains " + cardsData.getListNames(card).size() + " list(s)"))
                            .addDescription("input", 5, InventoryMenu.INPUT_RIGHT_CLICK.append(Component.text("more options")));
                    items.add(item);
                }
                addItemsToSelect(items);
            }

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, PlayerHandle player) {
                if (event.getClick() == ClickType.LEFT) {
                    openCardEditor(clickedOption.getPlainTextName(), player);
                } else if (event.getClick() == ClickType.RIGHT) {
                    createCardContext(clickedOption.getPlainTextName()).open(player);
                }
            }
        };
    }

    private BasicMenu createListPicker() {
        return new PaginatedSelectionMenu(getMenuBoard(), Component.text("Choose A List"), new ArrayList<>(), FilterType.DISPLAY_NAME)
        {
            private static final ItemTemplate CREATE_LIST = new ItemTemplate(51, ItemTypePaper.of(Material.EMERALD),
                    Component.text("New List").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

            @Override
            public void beforeOpening(PlayerHandle player) {
                TaskListData listsData = new TaskListData();

                addAction(CREATE_LIST, p -> createList(player));
                clearItems();

                List<ItemTemplate> items = new ArrayList<>();
                for (String list : listsData.getListNames()) {
                    ItemTemplate item = new ItemTemplate(ItemTypePaper.of(Material.PAPER), Component.text(list),
                            Component.text("This list contains " + listsData.getTaskCount(list) + " task(s)"))
                            .addDescription("input", 5, InventoryMenu.INPUT_RIGHT_CLICK.append(Component.text("more options")));
                    items.add(item);
                }
                addItemsToSelect(items);
            }

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, PlayerHandle player) {
                if (event.getClick() == ClickType.LEFT) {
                    openListEditor(clickedOption.getPlainTextName(), player);
                } else if (event.getClick() == ClickType.RIGHT) {
                    createListContext(clickedOption.getPlainTextName()).open(player);
                }
            }
        };
    }

    public void createCard(PlayerHandle player) {
        new UserInputMenu(getMenuBoard(), Component.text("Enter new card name"), (input) -> {
            if (!input.isEmpty())
                openCardEditor(input.toLowerCase().replace(" ", "_"), player);
        }, "name")
                .open(player);
    }

    public void createList(PlayerHandle player) {
        new UserInputMenu(getMenuBoard(), Component.text("Enter new list name"), (input) -> {
            if (!input.isEmpty())
                openListEditor(input.toLowerCase().replace(" ", "_"), player);
        }, "name")
                .open(player);
    }

    private void openCardEditor(String cardName, PlayerHandle player) {
        if (BingoCardData.DEFAULT_CARD_NAMES.contains(cardName)) {
            BingoPlayerSender.sendMessage(Component.text("Cannot edit default card, use right click to duplicate them instead!").color(NamedTextColor.RED), player);
            return;
        }
        CardEditorMenu editor = new CardEditorMenu(getMenuBoard(), cardName, cardsData);
        editor.open(player);
    }

    private void openListEditor(String listName, PlayerHandle player) {
        if (TaskListData.DEFAULT_LIST_NAMES.contains(listName)) {
            BingoPlayerSender.sendMessage(Component.text("Cannot edit default lists, use right click to duplicate them instead!").color(NamedTextColor.RED), player);
            return;
        }
        ListEditorMenu editor = new ListEditorMenu(getMenuBoard(), listName);
        editor.open(player);
    }


    public BasicMenu createCardContext(String cardName) {
        BasicMenu context = new BasicMenu(getMenuBoard(), Component.text(cardName), 1);
        context.addAction(new ItemTemplate(0, REMOVE_ICON, BingoReloaded.applyTitleFormat("Remove")), (args) -> {
                    cardsData.removeCard(cardName);
                    context.close(args.player());
                })
                .addAction(new ItemTemplate(1, COPY_ICON, BingoReloaded.applyTitleFormat("Duplicate")), (args) -> {
                    cardsData.duplicateCard(cardName);
                    context.close(args.player());
                })
                .addAction(new ItemTemplate(2, RENAME_ICON, BingoReloaded.applyTitleFormat("Change Name")), (args) -> {
                    new UserInputMenu(getMenuBoard(), Component.text("Change name to"), (input) -> {
                        cardsData.renameCard(cardName, input);
                        context.close(args.player());
                    }, cardName)
                            .open(args.player());
                })
                .addCloseAction(new ItemTemplate(8, SAVE_ICON, BingoReloaded.applyTitleFormat(BingoMessage.MENU_EXIT.asPhrase())));
        return context;
    }

    public BasicMenu createListContext(String listName) {
        TaskListData listsData = cardsData.lists();

        BasicMenu context = new BasicMenu(getMenuBoard(), Component.text(listName), 1);
        context.addAction(new ItemTemplate(0, REMOVE_ICON, BingoReloaded.applyTitleFormat("Remove")), (args) -> {
                    listsData.removeList(listName);
                    context.close(args.player());
                })
                .addAction(new ItemTemplate(1, COPY_ICON, BingoReloaded.applyTitleFormat("Duplicate")), (args) -> {
                    listsData.duplicateList(listName);
                    context.close(args.player());
                })
                .addAction(new ItemTemplate(2, RENAME_ICON, BingoReloaded.applyTitleFormat("Change Name")), (args) -> {
                    new UserInputMenu(getMenuBoard(), Component.text("Change name to"), (input) -> {
                        listsData.renameList(listName, input);
                        context.close(args.player());
                    }, listName)
                            .open(args.player());
                })
                .addCloseAction(new ItemTemplate(8, SAVE_ICON, BingoReloaded.applyTitleFormat(BingoMessage.MENU_EXIT.asPhrase())));
        return context;
    }
}