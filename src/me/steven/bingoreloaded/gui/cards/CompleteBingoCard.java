package me.steven.bingoreloaded.gui.cards;

import me.steven.bingoreloaded.item.BingoItem;
import me.steven.bingoreloaded.item.InventoryItem;
import me.steven.bingoreloaded.player.BingoTeam;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class CompleteBingoCard extends BingoCard
{

    public CompleteBingoCard(CardSize size)
    {
        super(size);
        InventoryItem item = new InventoryItem(0, Material.PAPER, "Complete-All Bingo Card", "First team to complete all items wins.");
        addOption(item);
    }

    @Override
    public boolean hasBingo(BingoTeam team)
    {
        return getCompleteCount(team) == size.fullCardSize;
    }

    @Override
    public CompleteBingoCard copy()
    {
        CompleteBingoCard card = new CompleteBingoCard(this.size);
        List<BingoItem> newItems = new ArrayList<>();
        for (BingoItem item : items)
        {
            newItems.add(new BingoItem(item.item));
        }
        card.items = newItems;
        return card;
    }
}
