package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.BingoSlotsData;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CardEditorUI extends ListPickerUI
{
    public final String cardName;
    private static final InventoryItem ADD_LIST = new InventoryItem(48, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "Add Item List", "");
    private ListValueEditorGUI valueEditorGUI;

    public CardEditorUI(String cardName, AbstractGUIInventory parent)
    {
        super(new ArrayList<>(), "Editing '" + cardName + "'", parent, FilterType.DISPLAY_NAME);
        this.cardName = cardName;

        fillOptions(new InventoryItem[]{ADD_LIST});
    }

    @Override
    public void onOptionClickedDelegate(final InventoryClickEvent event, InventoryItem clickedOption, Player player)
    {
        //if an ItemList attached to a card was clicked on
        if (clickedOption.getItemMeta() == null) return;

        String listName = clickedOption.getItemMeta().getDisplayName();
        valueEditorGUI = new ListValueEditorGUI(this, listName, BingoCardsData.getListMax(cardName, listName));
        valueEditorGUI.open(player);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        super.delegateClick(event, slotClicked, player, clickType);

        if (slotClicked == ADD_LIST.getSlot())
        {
            List<InventoryItem> items = new ArrayList<>();
            for (String category : BingoSlotsData.getListNames())
            {
                items.add(new InventoryItem(Material.PAPER, category, "Click to select"));
            }

            ListPickerUI listPicker = new ListPickerUI(items, "Pick A List", this, FilterType.DISPLAY_NAME)
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
        BingoCardsData.setList(cardName, result, 36, 0);
    }

    @Override
    public void open(HumanEntity player)
    {
        super.open(player);
        updateCardDisplay();
    }

    public void updateCardDisplay()
    {
        clearItems();

        List<InventoryItem> newItems = new ArrayList<>();
        for (String listName : BingoCardsData.getListsOnCard(cardName))
        {
            InventoryItem item = new InventoryItem(Material.MAP, listName, ChatColor.DARK_PURPLE + "Contains " + BingoSlotsData.getSlotCount(listName) + " item(s)");
            item.setAmount(BingoCardsData.getListMax(cardName, listName));
            newItems.add(item);
        }

        addItems(newItems.toArray(new InventoryItem[0]));

        applyFilter(getFilter());
    }
}
