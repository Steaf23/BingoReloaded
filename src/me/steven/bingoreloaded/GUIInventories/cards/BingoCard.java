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

    // CARD_SIZE must be between 1 and 6! Denotes the size of the length and width of the bingo card (i.e. size 5 will have 25 total spaces)
    public CardSize size;

    public String name = "Bingo Card";
    public ArrayList<BingoItem> items = new ArrayList<>();

    public BingoCard(CardSize size)
    {
        super(9 * size.cardSize, "Card Viewer");
        this.size = size;
    }

    public void generateCard(CardDifficulty difficulty)
    {
        List<Material> materials = BingoItem.ITEMS.get(difficulty);
        Collections.shuffle(materials);

        for (int i = 0; i < size.fullCardSize; i++)
        {
            items.add(new BingoItem(materials.get(i % materials.size())));
        }
    }

    public boolean completeItem(Material item, Material completeMaterial)
    {
        for (BingoItem bingoItem : items)
        {
            if (bingoItem.stack.getType() == item && bingoItem.isIncomplete())
            {
                bingoItem.complete(completeMaterial);
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

    public boolean hasBingo()
    {
        //check for rows and columns
        for (int y = 0; y < size.cardSize; y++)
        {
            boolean completedRow = true;
            boolean completedCol = true;
            for (int x = 0; x < size.cardSize; x++)
            {
                int indexRow = size.cardSize * y + x;
                if (items.get(indexRow).isIncomplete())
                {
                    completedRow = false;
                }

                int indexCol = size.cardSize * x + y;
                if (items.get(indexCol).isIncomplete())
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
            if (items.get(idx).isIncomplete())
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
                if (items.get(idx).isIncomplete())
                {
                    completedDiagonal2 = false;
                    break;
                }
            }
        }

        return completedDiagonal1 || completedDiagonal2;
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
