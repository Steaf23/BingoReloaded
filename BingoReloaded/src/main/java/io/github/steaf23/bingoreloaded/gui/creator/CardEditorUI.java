package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.gui.base.FilterType;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.PaginatedPickerMenu;
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
    public final BingoCardData cardsData;
    private static final MenuItem ADD_LIST = new MenuItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "Add Item List", "");
    private ListValueEditorGUI valueEditorGUI;

    public CardEditorUI(String cardName, MenuInventory parent, BingoCardData cardsData)
    {
        super(new ArrayList<>(), "Editing '" + cardName + "'", parent, FilterType.DISPLAY_NAME);
        this.cardName = cardName;
        this.cardsData = cardsData;
        addItem(ADD_LIST);
    }

    @Override
    public void onOptionClickedDelegate(final InventoryClickEvent event, MenuItem clickedOption, Player player)
    {
        //if an ItemList attached to a card was clicked on exists
        if (clickedOption.getItemMeta() == null) return;

        String listName = clickedOption.getItemMeta().getDisplayName();
        if (event.getClick() == ClickType.LEFT)
        {
            valueEditorGUI = new ListValueEditorGUI(this, listName, cardsData.getListMax(cardName, listName), cardsData.getListMin(cardName, listName));
            valueEditorGUI.open(player);
        }
        else if (event.getClick() == ClickType.RIGHT)
        {
            cardsData.removeList(cardName, listName);
            updateCardDisplay();
        }
    }

    @Override
    public void onItemClicked(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        super.onItemClicked(event, slotClicked, player, clickType);

        if (slotClicked == ADD_LIST.getSlot())
        {
            List<MenuItem> items = new ArrayList<>();
            for (String listName : cardsData.lists().getListNames())
            {
                items.add(new MenuItem(Material.PAPER, listName,
                        "This list contains " + cardsData.lists().getTaskCount(listName) + " task(s)",
                        ChatColor.GRAY + "Click to select"));
            }

            PaginatedPickerMenu listPicker = new PaginatedPickerMenu(items, "Pick A List", this, FilterType.DISPLAY_NAME)
            {
                @Override
                public void onOptionClickedDelegate(final InventoryClickEvent event, MenuItem clickedOption, Player player)
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
        cardsData.setList(cardName, result, cardsData.lists().getTaskCount(result), 1);
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

        addPickerContents(newItems.toArray(new MenuItem[0]));

        applyFilter(getFilter());
    }
}
