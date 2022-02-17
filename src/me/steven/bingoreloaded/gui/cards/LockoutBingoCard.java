package me.steven.bingoreloaded.gui.cards;

import me.steven.bingoreloaded.item.InventoryItem;
import org.bukkit.Material;
import org.bukkit.scoreboard.Team;

public class LockoutBingoCard extends BingoCard
{
    public int teamCount;

    public LockoutBingoCard(CardSize size)
    {
        super(size);
        InventoryItem cardInfo = new InventoryItem(0, Material.PAPER, "Lockout Bingo Card", "Complete the most items to win.", "When an item has been completed", "it cannot be complete by any other team.");
        addOption(cardInfo);
    }

    @Override
    public LockoutBingoCard copy()
    {
        return this;
    }

    @Override
    public boolean hasBingo(Team team)
    {
        int completeCount = getCompleteCount(team);
        if (teamCount > 1)
        {
            return completeCount >= Math.floor(size.fullCardSize / (double) teamCount) + 1;
        }
        return true;
    }
}
