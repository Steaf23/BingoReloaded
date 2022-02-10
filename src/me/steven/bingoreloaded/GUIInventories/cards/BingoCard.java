package me.steven.bingoreloaded.GUIInventories.cards;

import me.steven.bingoreloaded.*;
import me.steven.bingoreloaded.GUIInventories.AbstractGUIInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class BingoCard extends AbstractGUIInventory
{
    public enum CardDifficulty
    {
        EASY,
        NORMAL,
        HARD,
    }

    public CardSize size;

    public ArrayList<BingoItem> items = new ArrayList<>();

    public BingoCard(CardSize size)
    {
        super(9 * size.cardSize, "Card Viewer");
        this.size = size;
    }

    public void generateCard(CardDifficulty difficulty)
    {
        List<Material> materials = BingoReloaded.bingoItems.get(difficulty);
        Collections.shuffle(materials);

        BingoReloaded.broadcast(materials.toString());

        for (int i = 0; i < size.fullCardSize; i++)
        {
            items.add(new BingoItem(materials.get(i % materials.size())));
        }
    }

    /**
     * attempts to check off the submitted item from the team's card.
     * @param item the item to check completion of.
     * @param team the team submitting the item
     * @return if the item could be completed and wasn't already done so, return true.
     */
    public boolean completeItem(Material item, Team team)
    {
        for (BingoItem bingoItem : items)
        {
            if (bingoItem.stack.getType() == item && !bingoItem.isComplete(team))
            {
                bingoItem.complete(team);
                return true;
            }
        }

        return false;
    }

    public void showInventory(HumanEntity player)
    {
        for (int i = 0; i < items.size(); i++)
        {
            addOption(size.getCardInventorySlot(i), items.get(i).stack);
        }

        open(player);
    }

    public boolean hasBingo(Team team)
    {
        BingoReloaded.print("Your team (" + team.getDisplayName() + ChatColor.RESET + ") has collected " + getCompleteCount(team) + " items!", team);
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
     *
     * @param team
     * @return the amount of completed items for the given team
     */
    public int getCompleteCount(Team team)
    {
        int count = 0;
        for (BingoItem item : items)
        {
            if (item.isComplete(team)) count++;
        }

        return count;
    }

    @Override
    public void delegateClick(InventoryClickEvent event)
    {

    }

    @Override
    public void delegateDrag(InventoryDragEvent event)
    {

    }
}
