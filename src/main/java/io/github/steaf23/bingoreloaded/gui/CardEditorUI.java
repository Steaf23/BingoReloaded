package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.cardcreator.CardEntry;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.BingoSlotsData;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CardEditorUI extends ListPickerUI
{
    private final CardEntry card;
    private static final InventoryItem ADD_LIST = new InventoryItem(48, Material.EMERALD, "Add Item List", "");
    private static final InventoryItem SAVE_CARD = new InventoryItem(50, Material.DIAMOND, "Save and exit", "");

    private ListValueEditorGUI valueEditorGUI;

    public CardEditorUI(CardEntry card)
    {
        super(new ArrayList<>(), "Editing '" + card.getName() + "'", null, FilterType.DISPLAY_NAME);
        this.card = card;



        fillOptions(new InventoryItem[]{ADD_LIST, SAVE_CARD});
    }

    @Override
    public void onOptionClickedDelegate(final InventoryClickEvent event, InventoryItem clickedOption, Player player)
    {
        //if an ItemList attached to a card was clicked on
        if (clickedOption.getItemMeta() == null) return;

        String listName = clickedOption.getItemMeta().getDisplayName();
        valueEditorGUI = new ListValueEditorGUI(this, listName, card.getSlotLists().get(listName));
        valueEditorGUI.open(player);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        super.delegateClick(event, slotClicked, player);

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
        else if(slotClicked == SAVE_CARD.getSlot())
        {
            BingoCardsData.saveCard(card);
            close(player);
        }
    }

    public void getResultFromPicker(String result)
    {
        card.addItemList(result, 36);
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
        for (String listName : card.getSlotLists().keySet())
        {
            InventoryItem item = new InventoryItem(Material.MAP, listName, ChatColor.DARK_PURPLE + "Contains " + BingoSlotsData.getSlotCount(listName) + " item(s)");
            item.setAmount(card.getSlotLists().get(listName) == 0 ? 1 : card.getSlotLists().get(listName));
            newItems.add(item);
        }

        addItems(newItems.toArray(new InventoryItem[0]));

        applyFilter(getFilter());
    }

    public void updateListValues(String listName, int maxOccurrence)
    {
        card.getSlotLists().put(listName, maxOccurrence);
        updateCardDisplay();
    }
}
