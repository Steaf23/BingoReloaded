package me.steven.bingoreloaded.gui;

import me.steven.bingoreloaded.item.InventoryItem;
import me.steven.bingoreloaded.data.BingoCardsData;
import me.steven.bingoreloaded.data.BingoItemData;
import me.steven.bingoreloaded.cardcreator.CardEntry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CardEditorUI extends ItemPickerUI
{
    private final CardEntry card;
    private final InventoryItem addList;
    private final InventoryItem saveCard;

    private ListValueEditorGUI valueEditorGUI;

    public CardEditorUI(CardEntry card)
    {
        super(new ArrayList<>(), "Editing '" + card.name + "'", null);
        this.card = card;

        addList = new InventoryItem(48, Material.EMERALD, "Add Item List", "");
        saveCard = new InventoryItem(50, Material.DIAMOND, "Save and exit", "");

        fillOptions(new InventoryItem[]{addList, saveCard});
    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
    {
        //if an ItemList attached to a card was clicked on
        if (clickedOption.getItemMeta() == null) return;

        String listName = clickedOption.getItemMeta().getDisplayName();
        valueEditorGUI = new ListValueEditorGUI(this, listName, BingoCardsData.getListValues(card.name, listName));
        valueEditorGUI.open(player);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        super.delegateClick(event, slotClicked, player);

        if (slotClicked == addList.getSlot())
        {
            List<InventoryItem> items = new ArrayList<>();
            for (String category : BingoItemData.getCategories())
            {
                items.add(new InventoryItem(Material.PAPER, category, "Click to select"));
            }

            ItemPickerUI listPicker = new ItemPickerUI(items, "Pick A List", this)
            {
                @Override
                public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
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
        else if(slotClicked == saveCard.getSlot())
        {
            BingoCardsData.saveCard(card);
            close(player);
        }
    }

    public void getResultFromPicker(String result)
    {
        card.addItemList(result, 1);
    }

    @Override
    public void open(HumanEntity player)
    {
        updateCardDisplay();
        super.open(player);
    }

    public void updateCardDisplay()
    {
        clearItems();

        List<InventoryItem> newItems = new ArrayList<>();
        for (String listName : card.getItemLists().keySet())
        {
            InventoryItem item = new InventoryItem(Material.MAP, listName, ChatColor.DARK_PURPLE + "Contains " + BingoItemData.getItemCount(listName) + " item(s)");
            item.setAmount(card.getItemLists().get(listName));
            newItems.add(item);
        }

        addItems(newItems.toArray(new InventoryItem[0]));
        updatePage();
    }

    public void updateListValues(String listName, int maxOccurrence)
    {
        card.getItemLists().put(listName, maxOccurrence);
        updateCardDisplay();
    }
}
