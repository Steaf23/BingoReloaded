package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.core.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.core.data.TaskListsData;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.FilterType;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedPickerMenu;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CardEditorUI extends PaginatedPickerMenu
{
    public final String cardName;
    private static final InventoryItem ADD_LIST = new InventoryItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "Add Item List", "");
    private ListValueEditorGUI valueEditorGUI;

    public CardEditorUI(String cardName, MenuInventory parent)
    {
        super(new ArrayList<>(), "Editing '" + cardName + "'", parent, FilterType.DISPLAY_NAME);
        this.cardName = cardName;
        addOption(ADD_LIST);
    }

    @Override
    public void onOptionClickedDelegate(final InventoryClickEvent event, InventoryItem clickedOption, Player player)
    {
        //if an ItemList attached to a card was clicked on exists
        if (clickedOption.getItemMeta() == null) return;

        String listName = clickedOption.getItemMeta().getDisplayName();
        if (event.getClick() == ClickType.LEFT)
        {
            valueEditorGUI = new ListValueEditorGUI(this, listName, BingoCardsData.getListMax(cardName, listName), BingoCardsData.getListMin(cardName, listName));
            valueEditorGUI.open(player);
        }
        else if (event.getClick() == ClickType.RIGHT)
        {
            BingoCardsData.removeList(cardName, listName);
            updateCardDisplay();
        }
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        super.delegateClick(event, slotClicked, player, clickType);

        if (slotClicked == ADD_LIST.getSlot())
        {
            List<InventoryItem> items = new ArrayList<>();
            for (String listName : TaskListsData.getListNames())
            {
                items.add(new InventoryItem(Material.PAPER, listName,
                        "This list contains " + TaskListsData.getTasks(listName).size() + " task(s)",
                        ChatColor.GRAY + "Click to select"));
            }

            PaginatedPickerMenu listPicker = new PaginatedPickerMenu(items, "Pick A List", this, FilterType.DISPLAY_NAME)
            {
                @Override
                public void onOptionClickedDelegate(final InventoryClickEvent event, InventoryItem clickedOption, Player player)
                {
                    ItemMeta optionMeta = clickedOption.getItemMeta();

                    if(optionMeta != null)
                    {
                        getResultFromPicker(optionMeta.getDisplayName());
                    }
                    close(player);
                }
            };
            listPicker.open(player);
        }
    }

    public void getResultFromPicker(String result)
    {
        BingoCardsData.setList(cardName, result, TaskListsData.getTasks(result).size(), 1);
    }

    @Override
    public void handleOpen(final InventoryOpenEvent event)
    {
        super.handleOpen(event);
        updateCardDisplay();
    }

    public void updateCardDisplay()
    {
        clearItems();

        List<InventoryItem> newItems = new ArrayList<>();
        for (String listName : BingoCardsData.getLists(cardName))
        {
            InventoryItem item = new InventoryItem(Material.MAP, listName,
                    "This list contains " + TaskListsData.getTasks(listName).size() + " task(s)",
                    ChatColor.GRAY + "Left-click to edit distribution",
                    ChatColor.GRAY + "Right-click to remove this list");
            item.setAmount(Math.max(1, BingoCardsData.getListMax(cardName, listName)));
            newItems.add(item);
        }

        addItems(newItems.toArray(new InventoryItem[0]));

        applyFilter(getFilter());
    }
}
