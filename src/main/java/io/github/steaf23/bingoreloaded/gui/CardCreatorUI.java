package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.BingoSlotsData;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class CardCreatorUI extends AbstractGUIInventory
{
    public static final InventoryItem CARD = new InventoryItem(11, Material.FILLED_MAP, TITLE_PREFIX + "Edit Cards", "Click to view and edit bingo cards!");
    public static final InventoryItem ITEM = new InventoryItem(15, Material.PAPER, TITLE_PREFIX + "Edit Lists", "Click to view and edit bingo lists!");

    public CardCreatorUI(AbstractGUIInventory parent)
    {
        super(27, "Card Creator", parent);

        fillOptions(new InventoryItem[]{CARD, ITEM});
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == CARD.getSlot())
        {
            List<InventoryItem> items = new ArrayList<>();
            for (String card : BingoCardsData.getCardNames())
            {
                InventoryItem item = new InventoryItem(Material.FILLED_MAP, card);
                items.add(item);
            }

            ListPickerUI cardPicker = new ListPickerUI(items, "Choose A Card", this, FilterType.DISPLAY_NAME)
            {
                @Override
                public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
                {
                    openCardEditor(clickedOption.getItemMeta().getDisplayName(), player);
                }
            };
            cardPicker.open(player);
        }
        else if (slotClicked == ITEM.getSlot())
        {
            List<InventoryItem> items = new ArrayList<>();
            for (String list : BingoSlotsData.getListNames())
            {
                InventoryItem item = new InventoryItem(Material.PAPER, list);
                items.add(item);
            }

            ListPickerUI listPicker = new ListPickerUI(items, "Choose a list", this, FilterType.DISPLAY_NAME)
            {
                @Override
                public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
                {
                    openListEditor(clickedOption.getItemMeta().getDisplayName(), player);
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

    }
}