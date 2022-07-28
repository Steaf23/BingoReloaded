package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.item.AbstractCardSlot;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class CompleteBingoCard extends BingoCard
{
    public CompleteBingoCard(CardSize size, BingoGame game)
    {
        super(size, game);
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
        CompleteBingoCard card = new CompleteBingoCard(this.size, game);
        List<AbstractCardSlot> newItems = new ArrayList<>();
        for (AbstractCardSlot item : cardSlots)
        {
            newItems.add(item.copy());
        }
        card.cardSlots = newItems;
        return card;
    }
}
