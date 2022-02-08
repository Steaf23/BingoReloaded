package me.steven.bingoreloaded.cards;

import me.steven.bingoreloaded.BingoItem;
import org.bukkit.inventory.Inventory;

public class CompleteBingoCard extends BingoCard
{
    public CompleteBingoCard()
    {

    }

    @Override
    public boolean checkBingo()
    {
        boolean completed = true;
        for (BingoItem item : items)
        {
            if (!item.isCompleted())
            {
                completed = false;
                break;
            }
        }
        return completed;
    }
}
