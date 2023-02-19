package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.TaskListsData;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.gui.*;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.util.Message;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

// This class is used to navigate through the cards and lists.
// Uses a double ListPicker, one for cards and one for lists.
public class BingoCreatorUI extends AbstractGUIInventory
{
    public static final InventoryItem CARD = new InventoryItem(11, Material.FILLED_MAP, TITLE_PREFIX + "Edit Cards", "Click to view and edit bingo cards!");
    public static final InventoryItem LIST = new InventoryItem(15, Material.PAPER, TITLE_PREFIX + "Edit Lists", "Click to view and edit bingo lists!");

    public BingoCreatorUI(AbstractGUIInventory parent)
    {
        super(27, "Card Creator", parent);
        fillOptions(CARD, LIST);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == CARD.getSlot())
        {
            PaginatedPickerUI cardPicker = new PaginatedPickerUI(new ArrayList<>(), "Choose A Card", this, FilterType.DISPLAY_NAME)
            {
                private static final InventoryItem CREATE_CARD = new InventoryItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "New Card");

                @Override
                public void handleOpen(final InventoryOpenEvent event)
                {
                    super.handleOpen(event);
                    addOption(CREATE_CARD);
                    clearItems();

                    List<InventoryItem> items = new ArrayList<>();
                    for (String card : BingoCardsData.getCardNames())
                    {
                        InventoryItem item = new InventoryItem(Material.FILLED_MAP, card,
                                "This card contains " + BingoCardsData.getLists(card).size() + " list(s)",
                                ChatColor.GRAY + "Right-click for more options");
                        items.add(item);
                    }
                    addItems(items.toArray(new InventoryItem[]{}));
                }

                @Override
                public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
                {
                    if (slotClicked == CREATE_CARD.getSlot())
                    {
                        createCard(player);
                    }
                    super.delegateClick(event, slotClicked, player, clickType);
                }

                @Override
                public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
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
            PaginatedPickerUI listPicker = new PaginatedPickerUI(new ArrayList<>(), "Choose A List", this, FilterType.DISPLAY_NAME)
            {
                private static final InventoryItem CREATE_LIST = new InventoryItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "New List");

                @Override
                public void handleOpen(final InventoryOpenEvent event)
                {
                    super.handleOpen(event);
                    addOption(CREATE_LIST);
                    clearItems();

                    List<InventoryItem> items = new ArrayList<>();
                    for (String list : TaskListsData.getListNames())
                    {
                        InventoryItem item = new InventoryItem(Material.PAPER, list,
                                "This list contains " + TaskListsData.getTasks(list).size() + " tasks",
                                ChatColor.GRAY + "Right-click for more options");
                        items.add(item);
                    }
                    addItems(items.toArray(new InventoryItem[]{}));
                }

                @Override
                public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
                {
                    if (slotClicked == CREATE_LIST.getSlot())
                    {
                        createList(player);
                    }
                    super.delegateClick(event, slotClicked, player, clickType);
                }

                @Override
                public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
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
        CardEditorUI editor = new CardEditorUI(cardName, this);
        editor.open(player);
    }

    private void openListEditor(String listName, Player player)
    {
        ListEditorUI editor = new ListEditorUI(listName, this);
        editor.open(player);
    }

    public void createCard(Player player)
    {
        UserInputUI.open("Enter new card name", (input) -> {
            if (!input.equals(""))
                openCardEditor(input.toLowerCase().replace(" ", "_"), player);
        }, player, this);
    }

    public void createList(Player player)
    {
        UserInputUI.open("Enter new list name", (input) -> {
            if (!input.equals(""))
                openListEditor(input.toLowerCase().replace(" ", "_"), player);
        }, player, this);
    }

    public void createCardContext(String cardName, Player player, AbstractGUIInventory parent)
    {
        ContextMenuUI menu = new ContextMenuUI("What to do with '" + cardName + "'", parent);
        menu.addAction("Remove", Material.BARRIER, (clickType) -> {
            BingoCardsData.removeCard(cardName);
            return true;
        });
        menu.addAction("Duplicate", Material.SHULKER_SHELL, (clickType) -> {
            BingoCardsData.duplicateCard(cardName);
            return true;
        });
        menu.addAction("Change Name", Material.NAME_TAG, (clickType) -> {
            UserInputUI.open("Change name to", (input) -> {
                BingoCardsData.renameCard(cardName, input);
            }, player, parent, cardName);
            return false;
        });
        menu.open(player);
    }

    public void createListContext(String listName, Player player, AbstractGUIInventory parent)
    {
        ContextMenuUI menu = new ContextMenuUI("What to do with '" + listName + "'", parent);
        menu.addAction("Remove", Material.BARRIER, (clickType) -> {
            TaskListsData.removeList(listName);
            return true;
        });
        menu.addAction("Duplicate", Material.SHULKER_SHELL, (clickType) -> {
            TaskListsData.duplicateList(listName);
            return true;
        });
        menu.addAction("Change Name", Material.NAME_TAG, (clickType) -> {
            UserInputUI.open("Change name to", (input) -> {
                TaskListsData.renameList(listName, input);
            }, player, parent, listName);
            return false;
        });
        menu.open(player);
    }
}