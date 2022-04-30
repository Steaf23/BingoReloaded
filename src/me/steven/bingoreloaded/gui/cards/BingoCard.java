package me.steven.bingoreloaded.gui.cards;

import me.steven.bingoreloaded.item.BingoItem;
import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.gui.AbstractGUIInventory;
import me.steven.bingoreloaded.item.InventoryItem;
import me.steven.bingoreloaded.data.BingoItemData;
import me.steven.bingoreloaded.cardcreator.CardEntry;
import me.steven.bingoreloaded.player.BingoTeam;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BingoCard extends AbstractGUIInventory
{
    public CardSize size;

    public List<BingoItem> items = new ArrayList<>();

    public BingoCard(CardSize size)
    {
        super(9 * size.cardSize, "Card Viewer", null);
        this.size = size;
        InventoryItem cardInfoItem = new InventoryItem(0, Material.PAPER, "Regular Bingo Card", "First team to complete 1 line wins.", "Lines can span vertically, horizontally", "or vertically.");
        addOption(cardInfoItem);
    }

    public void generateCard(CardEntry cardData)
    {
        List<BingoItem> newItems = new ArrayList<>();

        for (String listName : cardData.getItemLists().keySet())
        {
            List<Material> materials = BingoItemData.getItems(listName);
            Collections.shuffle(materials);

            int value = cardData.getItemLists().get(listName);
            for (int i = 0; i < value; i++)
            {
                newItems.add(new BingoItem(materials.get(Math.floorMod(i, materials.size()))));
            }
        }

        newItems = trimItemList(newItems);

        //Lastly, shuffle and cut the list so it contains exactly enough items
        Collections.shuffle(newItems);
        items = newItems;
    }

    public List<BingoItem> trimItemList(List<BingoItem> newItems)
    {
        while (newItems.size() < size.fullCardSize)
        {
            newItems.add(new BingoItem(Material.DIRT));
        }
        return newItems.subList(0, size.fullCardSize);
    }

    /**
     * attempts to check off the submitted item from the team's card.
     * @param item the item to check completion of.
     * @param team the team submitting the item
     * @return if the item could be completed and wasn't already done so, return true.
     */
    public boolean completeItem(Material item, BingoTeam team, int time)
    {
        for (BingoItem bingoItem : items)
        {
            if (bingoItem.stack.getType() == item && !bingoItem.isComplete(team))
            {
                bingoItem.complete(team, time);
                return true;
            }
        }
        return false;
    }

    public void showInventory(HumanEntity player)
    {
        for (int i = 0; i < items.size(); i++)
        {
            addOption(items.get(i).stack.inSlot(size.getCardInventorySlot(i)));
        }

        open(player);
    }

    public boolean hasBingo(BingoTeam team)
    {
        BingoReloaded.print("Your team (" + team.getName() + ChatColor.RESET + ") has collected " + getCompleteCount(team) + " items!", team);
        //check for rows and columns
        for (int y = 0; y < size.cardSize; y++)
        {
            boolean completedRow = true;
            boolean completedCol = true;
            for (int x = 0; x < size.cardSize; x++)
            {
                int indexRow = size.cardSize * y + x;
                if (!items.get(indexRow).isComplete(team))
                {
                    completedRow = false;
                }

                int indexCol = size.cardSize * x + y;
                if (!items.get(indexCol).isComplete(team))
                {
                    completedCol = false;
                }
            }

            if (completedRow || completedCol)
            {
                return true;
            }
        }

        // check for diagonals
        boolean completedDiagonal1 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.cardSize + 1)
        {
            if (!items.get(idx).isComplete(team))
            {
                completedDiagonal1 = false;
                break;
            }
        }

        boolean completedDiagonal2 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.cardSize - 1)
        {
            if (idx != 0 && idx != size.fullCardSize - 1)
            {
                if (!items.get(idx).isComplete(team))
                {
                    completedDiagonal2 = false;
                    break;
                }
            }
        }

        return completedDiagonal1 || completedDiagonal2;
    }

    /**
     * @param team The team.
     * @return The amount of completed items for the given team.
     */
    public int getCompleteCount(BingoTeam team)
    {
        int count = 0;
        for (BingoItem item : items)
        {
            if (item.isComplete(team)) count++;
        }

        return count;
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {

    }

    public BingoCard copy()
    {
        BingoCard card = new BingoCard(this.size);
        List<BingoItem> newItems = new ArrayList<>();
        for (BingoItem item : items)
        {
            newItems.add(new BingoItem(item.item));
        }
        card.items = newItems;
        return card;
    }
}
